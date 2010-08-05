package org.infoscoop.batch.migration;

import java.sql.Connection;
import java.util.List;

import org.apache.tools.ant.Task;

public abstract class Migration {
	protected Task task;
	
	protected List sqls; 
	public void setSQLs(List sqls){
		this.sqls = sqls;
	}
	
	public abstract void execute( Connection connection ) throws Exception;
	//public abstract void rollback( Connection connection ) throws Exception;

	public void setTask(Task task) {
		this.task = task;
		
	}
}
