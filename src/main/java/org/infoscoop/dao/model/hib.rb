Dir.glob("*.java").sort.each do |f|
  puts "\t\t\t\t<value>org.infoscoop.dao.model.#{f.split(".")[0]}</value>"
end