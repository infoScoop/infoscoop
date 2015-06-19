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

package org.infoscoop.service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.api.dao.OAuth2ProviderClientDetailDAO;
import org.infoscoop.dao.AdminRoleDAO;
import org.infoscoop.dao.ForbiddenURLDAO;
import org.infoscoop.dao.GadgetDAO;
import org.infoscoop.dao.GadgetIconDAO;
import org.infoscoop.dao.HolidaysDAO;
import org.infoscoop.dao.I18NDAO;
import org.infoscoop.dao.OAuthCertificateDAO;
import org.infoscoop.dao.PortalAdminsDAO;
import org.infoscoop.dao.PortalLayoutDAO;
import org.infoscoop.dao.PropertiesDAO;
import org.infoscoop.dao.ProxyConfDAO;
import org.infoscoop.dao.SearchEngineDAO;
import org.infoscoop.dao.SquareDAO;
import org.infoscoop.dao.StaticTabDAO;
import org.infoscoop.dao.TabLayoutDAO;
import org.infoscoop.dao.WidgetConfDAO;
import org.infoscoop.dao.model.Adminrole;
import org.infoscoop.dao.model.Portaladmins;
import org.infoscoop.util.SpringUtil;

public class SquareService {
	private static Log log = LogFactory.getLog(SquareService.class);
	private static final String SQUAREID_DEFAULT = "default";

	// DAO
	private SquareDAO squareDAO;
	private AdminRoleDAO adminRoleDAO;
	private ForbiddenURLDAO forbiddenURLDAO;
	private GadgetDAO gadgetDAO;
	private GadgetIconDAO gadgetIconDAO;
	private HolidaysDAO holidaysDAO;
	private I18NDAO i18NDAO;
	private OAuthCertificateDAO oauthCertificateDAO;
	private PortalAdminsDAO portalAdminsDAO;
	private PortalLayoutDAO portalLayoutDAO;
	private PropertiesDAO propertiesDAO;
	private ProxyConfDAO proxyConfDAO;
	private SearchEngineDAO searchEngineDAO;
	private TabLayoutDAO tabLayoutDAO;
	private WidgetConfDAO widgetConfDAO;
	private StaticTabDAO staticTabDAO;
	private OAuth2ProviderClientDetailDAO oauth2ProviderClientDetailDAO;


//	insert into iscoop.IS_I18NLOCALES (type, country, lang, square_id) SELECT type, country, lang, "unirita" from iscoop.IS_I18NLOCALES;
//	insert into iscoop.IS_MENUS (type, data, square_id) SELECT type, data, "unirita" from iscoop.IS_MENUS;


	public SquareDAO getSquareDAO() {
		return squareDAO;
	}

	public void setSquareDAO(SquareDAO squareDAO) {
		this.squareDAO = squareDAO;
	}

	public AdminRoleDAO getAdminRoleDAO() {
		return adminRoleDAO;
	}

	public void setAdminRoleDAO(AdminRoleDAO adminRoleDAO) {
		this.adminRoleDAO = adminRoleDAO;
	}

	public ForbiddenURLDAO getForbiddenURLDAO() {
		return forbiddenURLDAO;
	}

	public void setForbiddenURLDAO(ForbiddenURLDAO forbiddenURLDAO) {
		this.forbiddenURLDAO = forbiddenURLDAO;
	}

	public GadgetDAO getGadgetDAO() {
		return gadgetDAO;
	}

	public void setGadgetDAO(GadgetDAO gadgetDAO) {
		this.gadgetDAO = gadgetDAO;
	}

	public GadgetIconDAO getGadgetIconDAO() {
		return gadgetIconDAO;
	}

	public void setGadgetIconDAO(GadgetIconDAO gadgetIconDAO) {
		this.gadgetIconDAO = gadgetIconDAO;
	}

	public HolidaysDAO getHolidaysDAO() {
		return holidaysDAO;
	}

	public void setHolidaysDAO(HolidaysDAO holidaysDAO) {
		this.holidaysDAO = holidaysDAO;
	}

	public I18NDAO getI18NDAO() {
		return i18NDAO;
	}

	public void setI18NDAO(I18NDAO i18NDAO) {
		this.i18NDAO = i18NDAO;
	}

	public OAuthCertificateDAO getOauthCertificateDAO() {
		return oauthCertificateDAO;
	}

	public void setOauthCertificateDAO(OAuthCertificateDAO oauthCertificateDAO) {
		this.oauthCertificateDAO = oauthCertificateDAO;
	}

	public PortalAdminsDAO getPortalAdminsDAO() {
		return portalAdminsDAO;
	}

	public void setPortalAdminsDAO(PortalAdminsDAO portalAdminsDAO) {
		this.portalAdminsDAO = portalAdminsDAO;
	}

	public PortalLayoutDAO getPortalLayoutDAO() {
		return portalLayoutDAO;
	}

	public void setPortalLayoutDAO(PortalLayoutDAO portalLayoutDAO) {
		this.portalLayoutDAO = portalLayoutDAO;
	}

	public PropertiesDAO getPropertiesDAO() {
		return propertiesDAO;
	}

	public void setPropertiesDAO(PropertiesDAO propertiesDAO) {
		this.propertiesDAO = propertiesDAO;
	}

	public ProxyConfDAO getProxyConfDAO() {
		return proxyConfDAO;
	}

	public void setProxyConfDAO(ProxyConfDAO proxyConfDAO) {
		this.proxyConfDAO = proxyConfDAO;
	}

	public SearchEngineDAO getSearchEngineDAO() {
		return searchEngineDAO;
	}

	public void setSearchEngineDAO(SearchEngineDAO searchEngineDAO) {
		this.searchEngineDAO = searchEngineDAO;
	}

	public TabLayoutDAO getTabLayoutDAO() {
		return tabLayoutDAO;
	}

	public void setTabLayoutDAO(TabLayoutDAO tabLayoutDAO) {
		this.tabLayoutDAO = tabLayoutDAO;
	}

	public WidgetConfDAO getWidgetConfDAO() {
		return widgetConfDAO;
	}

	public void setWidgetConfDAO(WidgetConfDAO widgetConfDAO) {
		this.widgetConfDAO = widgetConfDAO;
	}

	public StaticTabDAO getStaticTabDAO() {
		return staticTabDAO;
	}

	public void setStaticTabDAO(StaticTabDAO staticTabDAO) {
		this.staticTabDAO = staticTabDAO;
	}

	public OAuth2ProviderClientDetailDAO getOauth2ProviderClientDetailDAO() {
		return oauth2ProviderClientDetailDAO;
	}

	public void setOauth2ProviderClientDetailDAO(OAuth2ProviderClientDetailDAO oauth2ProviderClientDetailDAO) {
		this.oauth2ProviderClientDetailDAO = oauth2ProviderClientDetailDAO;
	}

	public static SquareService getHandle() {
		return (SquareService) SpringUtil.getBean("SquareService");
	}

	@SuppressWarnings("unchecked")
	public void createSquare(String squareId) {
		this.squareDAO.create(squareId, squareId, "");
		this.forbiddenURLDAO.copySquare(squareId, SQUAREID_DEFAULT);
		this.gadgetDAO.copySquare(squareId, SQUAREID_DEFAULT);
		this.gadgetIconDAO.copySquare(squareId, SQUAREID_DEFAULT);
		
		Map<Integer, Integer> roleIdMap = new HashMap<Integer, Integer>();
		
		// copy Adminrole
		List<Adminrole> adminRoleList = adminRoleDAO.select(SQUAREID_DEFAULT);
		Iterator<Adminrole> rolesIte = adminRoleList.iterator();
		while(rolesIte.hasNext()){
			Adminrole adminRole = rolesIte.next();
			String orgId = adminRole.getId();
			String newId = adminRoleDAO.insert(null, adminRole.getName(), adminRole.getPermission(), adminRole.isAllowDelete(), squareId, new Boolean(true));
			roleIdMap.put(new Integer(orgId), new Integer(newId));
		}
		
		// copy PortalAdmins
		List<Portaladmins> portalAdminList = portalAdminsDAO.select(SQUAREID_DEFAULT);
		Iterator<Portaladmins> adminsIte = portalAdminList.iterator();
		while(adminsIte.hasNext()){
			Portaladmins portalAdmin = adminsIte.next();
			Integer orgRoleId = portalAdmin.getRoleid();
			portalAdminsDAO.insert(portalAdmin.getUid(), roleIdMap.get(orgRoleId), squareId);
		}
		
	}
	
}
