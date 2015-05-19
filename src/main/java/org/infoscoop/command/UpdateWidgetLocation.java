/* infoScoop OpenSource
 * Copyright (C) 2010 Beacon IT Inc.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * as published by the Free Software Foundation.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0-standalone.html>.
 */

package org.infoscoop.command;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.command.util.XMLCommandUtil;
import org.infoscoop.context.UserContext;
import org.infoscoop.dao.TabDAO;
import org.infoscoop.dao.WidgetDAO;
import org.infoscoop.dao.model.Widget;

/**
 * The command class to update an infomation of widget's location.
 * 
 * @author nakata
 */
public class UpdateWidgetLocation extends XMLCommandProcessor {

    private Log log = LogFactory.getLog(this.getClass());

    /**
     * create a new object of UpdateWidgetLocation.
     */
    public UpdateWidgetLocation() {

    }

    /**
     * udpate the information of the widget's location.
     * 
     * @param uid
     *            a userId that is target of operation.
     * @param el
     *             The element of request command. Attributes of "widgetId", "targetColumn", and "sibling" are necessary for the Element. <BR>
     *            <BR>
     *             example of input element：<BR>
     * 
     * <pre>
     *  &lt;command type=&quot;UpdateWidgetLocation&quot; id=&quot;UpdateWidgetLocation_w_4&quot; widgetId=&quot;w_4&quot; targetColumn=&quot;3&quot; sibling=&quot;w_1&quot;/&gt;
     * </pre>
     */

	public void execute() {

        String commandId = super.commandXml.getAttribute("id").trim();
        String widgetId = super.commandXml.getAttribute("widgetId").trim();
        String tabId = super.commandXml.getAttribute("tabId").trim();
        String targetColumn = super.commandXml.getAttribute("targetColumn").trim();
        String parent = super.commandXml.getAttribute("parent").trim();
        String sibling = super.commandXml.getAttribute("sibling").trim();
        String squareid = UserContext.instance().getUserInfo().getCurrentSquareId();
        
        if(log.isInfoEnabled()){
        	String logMsg = "uid:[" + uid + "]: processXML: tabId:[" + tabId
					+ "], widgetId:[" + widgetId + "], targetColumn:["
					+ targetColumn + "], parent:[" + parent + "], sibling:[" + sibling + "]";
        	log.info(logMsg);
        }
        if (widgetId == null || widgetId == "") {
            String reason = "It's an unjust widgetId．widgetId:[" + widgetId + "]";
            this.result = XMLCommandUtil.createResultElement(uid, "processXML",
                    log, commandId, false, reason);
            return;
        }

        if (targetColumn != null && !"".equals(targetColumn) && !XMLCommandUtil.isNumberValue(targetColumn)) {
            String reason = "The value of column is unjust．targetColumn:[" + targetColumn + "]";
            this.result = XMLCommandUtil.createResultElement(uid, "processXML",
                    log, commandId, false, reason);
            return;
        }
        
        TabDAO tabDAO = TabDAO.newInstance();
        WidgetDAO widgetDAO = WidgetDAO.newInstance();
        
        Widget widget = widgetDAO.getWidget( uid,tabId,widgetId,squareid );
        if (widget == null) {
            String reason = "Not found the information of the widget(wigetID) that is origin of movement．widgetId:["
                    + widgetId + "]";
            this.result =  XMLCommandUtil.createResultElement(uid, "processXML",
                    log, commandId, false, reason);
            return;
        }
        
        Widget oldNextSibling = tabDAO.getWidgetBySibling( uid,tabId,widget.getWidgetid(),squareid );            	
        if(oldNextSibling != null){
        	oldNextSibling.setSiblingid(widget.getSiblingid());
        	widgetDAO.updateWidget(oldNextSibling);
        }

        Widget newNextSibling;
        if(parent != null && !"".equals(parent)){
			newNextSibling = tabDAO.getSubWidgetBySibling( uid,tabId,sibling,parent,widgetId,squareid );
		} else {
			newNextSibling = tabDAO.getColumnWidgetBySibling( uid,tabId,sibling,Integer.valueOf( targetColumn ),widgetId,squareid );
		}
        
        if(newNextSibling != null){
        	newNextSibling.setSiblingid(widget.getWidgetid());
        	log.info("Replace siblingId of [" + newNextSibling.getWidgetid() + "] to " + widget.getWidgetid());
        	widgetDAO.updateWidget(newNextSibling);
        }
        
        widget.setSiblingid(sibling);
      
        try{
        	widget.setColumn(new Integer(targetColumn));
        }catch(NumberFormatException e){
        	widget.setColumn(null);
        }

        if(parent != null)
        	widget.setParentid(parent);
        
//        WidgetDAO.newInstance().updateWidget(widget);
        
        this.result = XMLCommandUtil.createResultElement(uid, "processXML", log,
                commandId, true, null);

	}
}
