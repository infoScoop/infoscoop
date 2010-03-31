tables = []
open("mysqlinit.sql") {|file|
  while l = file.gets
    if /^--|^[\s]*primary key|^[\s]*foreign key /i =~ l
    elsif /^create table ([\w]+) \(/ =~ l
      tables << {:name => $1, :columns => [], :uniques => [], :indexes => []}
      #  constraint is_widgets_unique unique (`UID`, tabid, widgetId, deleteDate)
    elsif /^[\s]*constraint ([\w]+) unique \(([\w, `]+)\)/ =~ l
      tables.last[:uniques] << {
        :name => $1,
        :columns => $2.gsub('`','').split(/\s*,\s*/)
      }
    elsif /^[\s]*create index ([\w]+) on ([\w]+)\(`?([\w]+)`?\)/ =~ l
      tables.last[:indexes] << {
        :name => $1,
        :table => $2,
        :column => $3
      }
    elsif /`?([\w]+)`?[ ]+([\w]+)(\(([0-9]+)\))?/
      tables.last[:columns] << {
        :name => $1,
        :type => $2,
        :limit => $4,
        :null => /not null/ =~ l
      }
    end
  end
}

tables.sort!{|a, b| a[:name] <=> b[:name]}

id_mapping = {
  "IS_I18N" => "name",
  "IS_PROPERTIES" => "name",
  "IS_TABS" => "tabId"
}
table_suffix = "_2_0"

rf = File.open("2.0to2.1_rename.ddl", 'w')
lf = File.open("2.0to2.1_load.ddl", 'w')
cf = File.open("2.0to2.1_clean.ddl", 'w')
df = File.open("2.0to2.1_delete.ddl", 'w')
af = File.open("2.0to2.1_dropall.ddl", 'w')

tables.each {|t|
  columns1 = ''
  columns2 = ''
  t[:columns].each_with_index {|c, index|
    if c[:name] =~ /^id$/i
      new_name = id_mapping[t[:name].upcase]
      unless new_name.nil?
        c[:new_name] = new_name
      else
        next
      end
    end
    if index > 0 && columns1 != ""
      columns1 += ", "
      columns2 += ", "
    end
    columns1 += "`#{c[:name]}`"
    columns2 += "`#{c[:new_name] || c[:name]}`"
  }
  t[:indexes].each {|i|
    rf.puts "ALTER TABLE #{i[:table]} DROP INDEX #{i[:name]};"
  }
  rf.puts "ALTER TABLE #{t[:name]} RENAME TO #{t[:name]}#{table_suffix};"
  lf.puts "INSERT INTO #{t[:name]}(#{columns2}) SELECT #{columns1} FROM #{t[:name]}#{table_suffix};"
  cf.puts "DROP TABLE #{t[:name]}#{table_suffix};"
  df.puts "DELETE FROM #{t[:name]};"
  af.puts "DROP TABLE #{t[:name]};"
  af.puts "DROP TABLE #{t[:name]}#{table_suffix};"
}
rf.close
lf.close
cf.close
df.close
af.close