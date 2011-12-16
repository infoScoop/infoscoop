package org.infoscoop.batch.migration;

import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class HibernateBeansTask extends Task {
	public HibernateBeansTask() {
		// TODO Auto-generated constructor stub
	}
	
	private String typeName;
	public void setType( String typeName ) throws Exception {
		this.typeName = typeName;
	}
	
	private String where;
	public void setWhere( String where ) {
		this.where = where;
	}
	
	private List<BeanTask> tasks = new ArrayList<BeanTask>();
	public BeanTask createTask() {
		BeanTask task = new GenericBeanTask();
		tasks.add( task );
		
		return task;
	}
	public void add( BeanTask task ) {
		tasks.add( task );
	}
	
	public void execute() throws BuildException {
		SessionFactory sessionFactory = ( SessionFactory )SQLTask.getContext().getBean("sessionFactory");
		Session session = sessionFactory.openSession();
		
		try {
			String queryString = "from "+typeName;
			if( where != null ) queryString += " where "+where;
			
			log("QueryString: "+queryString );
			List objects = session.createQuery( queryString ).list();
			for( BeanTask task : tasks ) {
				if( task instanceof BeanTask2 ) {
					super.getProject().log("Prepare Task: "+task );
					(( BeanTask2 )task ).prepare( super.getProject() );
				}
			}
			
			for( int i=0;i<objects.size();i++ ) {
				Object object = objects.get( i );
				
				for( BeanTask task : tasks ) {
					super.getProject().log("Begin Task: "+task );
					
					task.execute( super.getProject(),object );
				}
				
				session.update( object );
			}
			session.flush();
			session.clear();
			
			for( BeanTask task : tasks ) {
				if( task instanceof BeanTask2 ) {
					super.getProject().log("Finish Task: "+task );
					(( BeanTask2 )task ).finish( super.getProject() );
				}
			}
			
			log("total "+objects.size()+" objects executed.",Project.MSG_INFO );
		} catch( Exception ex ) {
			throw new BuildException( ex );
		} finally {
			session.close();
		}
	}
	
	public static interface BeanTask {
		public void execute( Project project,Object object ) throws BuildException;
	}
	public static interface BeanTask2 extends BeanTask {
		public void prepare( Project project ) throws BuildException;
		public void finish( Project project ) throws BuildException;
	}
	public static class GenericBeanTask implements BeanTask2 {
		private BeanTask beanTask;
		public void setClass( String className ) throws BuildException {
			try {
				beanTask = ( BeanTask )Class.forName( className ).newInstance();
			} catch( Exception ex ) {
				throw new BuildException( ex );
			}
		}
		
		public void execute( Project project,Object object ) throws BuildException {
			beanTask.execute( project,object );
		}

		public void prepare( Project project ) throws BuildException {
			if( beanTask instanceof BeanTask2 )
				(( BeanTask2 )beanTask ).prepare( project );
		}
		public void finish( Project project ) throws BuildException {
			if( beanTask instanceof BeanTask2 )
				(( BeanTask2 )beanTask ).finish( project );
		}
		
		public String toString() {
			return "GenericBeanTask["+beanTask+"]";
		}
	}
}
