package org.infoscoop.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.infoscoop.acl.ISPrincipal;
import org.infoscoop.acl.SecurityController;
import org.infoscoop.dao.NotificationDAO;
import org.infoscoop.dao.SquareDAO;
import org.infoscoop.dao.model.Notification;
import org.infoscoop.dao.model.Square;
import org.infoscoop.util.SpringUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class NotificationService {
	
	private NotificationDAO notificationDAO;
	private SquareDAO squareDAO;
	
	/**
	 * for ObjectMapper
	 * @author b1738
	 *
	 */
	static interface SquareView {
	    @JsonIgnore String getHashCode();
	    @JsonIgnore String getDescription();
	    @JsonIgnore String getLastmodified();
	    @JsonIgnore String getOwner();
	    @JsonIgnore String getMaxUserNum();
	    @JsonIgnore String getParentSquareId();
	}
	
	public NotificationDAO getNotificationDAO() {
		return notificationDAO;
	}

	public void setNotificationDAO(NotificationDAO notificationDAO) {
		this.notificationDAO = notificationDAO;
	}
	
	public SquareDAO getSquareDAO() {
		return squareDAO;
	}

	public void setSquareDAO(SquareDAO squareDAO) {
		this.squareDAO = squareDAO;
	}
	
	public static NotificationService getHandle() {
		return (NotificationService) SpringUtil.getBean("NotificationService");
	}
	
	public List<Notification> getMyNotifications(int offset, int limit, Date startDate, List<String> serviceIds) {
		ISPrincipal p = SecurityController.getPrincipalByType("UIDPrincipal");
		String uid = p.getName();
		List<Object[]> serviceInfos = squareDAO.getParentSquaresInfoByUid(uid);
		
		return notificationDAO.select(offset, limit, startDate, serviceIds);
	}
	
	public String getMyNotificationsJSON(int offset, int limit, Date startDate) throws JsonGenerationException, JsonMappingException, IOException, JSONException {
		ISPrincipal p = SecurityController.getPrincipalByType("UIDPrincipal");
		String uid = p.getName();
		List<Object[]> serviceInfos = squareDAO.getParentSquaresInfoByUid(uid);
		
		if(serviceInfos.size() == 0)
			return "[]";
		
		List<String> serviceIds = new ArrayList<String>();
		Map<String, String> squareNameMap = new HashMap<String, String>();
		for (Object[] row : serviceInfos) {
		    String serviceId = (String) row[0];
		    serviceIds.add(serviceId);
		    
		    String squareName = (String) row[1];
		    squareNameMap.put(serviceId, squareName);
		}
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.getSerializationConfig().addMixInAnnotations(Square.class, SquareView.class);
		
		String json = mapper.writeValueAsString(getMyNotifications(offset, limit, startDate, serviceIds));
		
		JSONArray jsonArray = new JSONArray(json);
		
		for(int i=0;i<jsonArray.length();i++){
			JSONObject jsonObj = jsonArray.getJSONObject(i);
			String serviceId = jsonObj.getString("squareId");
			jsonObj.put("serviceSquareName", squareNameMap.get(serviceId));
		}
		
		return jsonArray.toString();
	}
	
}
