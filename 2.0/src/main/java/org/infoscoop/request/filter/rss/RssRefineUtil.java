package org.infoscoop.request.filter.rss;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jruby.Ruby;
import org.jruby.javasupport.JavaEmbedUtils;
import org.jruby.runtime.builtin.IRubyObject;

/**
 * The utility class which works to narrow it down
 * @author a-kimura
 *
 */
public class RssRefineUtil {
	private static Log log = LogFactory.getLog(RssRefineUtil.class);

	public static boolean matchCreator(String creator, String creatorFilter) {
		if (creatorFilter == null)
			return true;
		if (creator == null)
			return false;
		return creator.trim().replaceAll("^　*","").indexOf(creatorFilter) == 0;
	}

	public static boolean matchCategory(List<String> category,
			String categoryFilter) {
		if (categoryFilter == null)
			return true;
		if (category == null || category.size() == 0)
			return false;
		return category.contains(categoryFilter);
	}

	public static boolean matchTitle(String title, String titleFilter) {
		if (titleFilter == null)
			return true;
		if (title == null)
			return false;
		title = title.trim();
		StringBuffer boolExp = new StringBuffer();
		String[] words = titleFilter.split(" ");
		boolean hasOp = true;
		Pattern p = Pattern.compile("(-)?(\\()?([^\\)]*)(\\))?");
		for (int i = 0; i < words.length; i++) {
			String word = words[i];
			if (word.equals("OR")) {
				boolExp.append(" || ");
				hasOp = true;
				continue;
			}
			Matcher m = p.matcher(word);
			if (m.matches()) {
				word = m.group(3);
				if (word == null || word.length() == 0)
					continue;
				if (!hasOp)
					boolExp.append(" && ");
				String minus = m.group(1);
				String startGrp = m.group(2);
				String endGrp = m.group(4);
				boolExp.append(" ");
				if (startGrp != null)
					boolExp.append(startGrp);
				if (minus != null && minus.length() > 0)
					boolExp.append("!");
				boolExp.append("'").append(title).append("'.include?('");
				boolExp.append(word.trim()).append("')");
				if (endGrp != null)
					boolExp.append(endGrp);
				boolExp.append(" ");
				hasOp = false;
			}
		}
		
		if (log.isDebugEnabled())
			log.debug("titleFilter = " + boolExp);
		try {
			Ruby ruby = Ruby.getGlobalRuntime();
			// Ruby ruby = Ruby.newInstance();
			IRubyObject result = ruby.evalScriptlet(boolExp.toString());
			Boolean boolResult = (Boolean) JavaEmbedUtils.rubyToJava(ruby,
					result, Boolean.class);
			return boolResult.booleanValue();
		} catch (Exception e) {
			log.warn("Failed to evaluate script. " + e.getMessage() + " : "
					+ boolExp);
			return false;
		}
	}
}
