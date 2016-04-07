package org.infoscoop.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
	
	public List<Notification> getMyNotifications(int offset, int limit, Date startDate) {
		ISPrincipal p = SecurityController.getPrincipalByType("UIDPrincipal");
		String uid = p.getName();
		List<String> serviceIds = squareDAO.getParentSquaresIDByUid(uid);
		
		if(serviceIds.size() == 0)
			return new ArrayList<Notification>();
		
		return notificationDAO.select(offset, limit, startDate, serviceIds);
	}
	
	public String getMyNotificationsJSON(int offset, int limit, Date startDate) throws JsonGenerationException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.getSerializationConfig().addMixInAnnotations(Square.class, SquareView.class);
		String json = mapper.writeValueAsString(getMyNotifications(offset, limit, startDate));
		return json;
	}
	
}
