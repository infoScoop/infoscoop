package org.infoscoop.dao.model.base;

import java.util.Date;

public class BaseNotification implements java.io.Serializable {
	
    private static final long serialVersionUID = 1L;
    private Integer id;
    private String type = NOTIFICATION_TYPE.GLOBAL.name();
    private String squareId;
    private String body;
    private Date lastmodified;
	private org.infoscoop.dao.model.Square square;
    
	public static String PROP_TYPE = "type";
	public static String PROP_SQUARE_ID = "squareId";
	public static String PROP_LASTMODIFIED = "lastmodified";
	
    public static enum NOTIFICATION_TYPE {
    	GLOBAL,
    	SERVICE
    }
    
    public BaseNotification() {
    }

    public BaseNotification(String type) {
        this.type = type;
    }

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSquareId() {
		return squareId;
	}

	public void setSquareId(String squareId) {
		this.squareId = squareId;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public Date getLastmodified() {
		return lastmodified;
	}

	public void setLastmodified(Date lastmodified) {
		this.lastmodified = lastmodified;
	}

	public org.infoscoop.dao.model.Square getSquare() {
		return square;
	}

	public void setSquare(org.infoscoop.dao.model.Square square) {
		this.square = square;
	}

}
