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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.account.AuthenticationService;
import org.infoscoop.account.IAccount;
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
import org.infoscoop.dao.SiteAggregationMenuDAO;
import org.infoscoop.dao.SquareDAO;
import org.infoscoop.dao.StaticTabDAO;
import org.infoscoop.dao.TabLayoutDAO;
import org.infoscoop.dao.WidgetConfDAO;
import org.infoscoop.dao.model.Adminrole;
import org.infoscoop.dao.model.Square;
import org.infoscoop.properties.InfoScoopProperties;
import org.infoscoop.util.SpringUtil;

public class SquareService {
	private static Log log = LogFactory.getLog(SquareService.class);
	public static final String SQUARE_ADMIN_ROLE_NAME = "squareAdmin";
	private static final String SQUARE_MAX_USER_NUMBER = "square.max.user.number";
	private static final String DEFAULT_MAX_USER = "10";

	// DAO
	private SquareDAO squareDAO;
	private AdminRoleDAO adminRoleDAO;
	private ForbiddenURLDAO forbiddenURLDAO;
	private GadgetDAO gadgetDAO;
	private GadgetIconDAO gadgetIconDAO;
	private HolidaysDAO holidaysDAO;
	private I18NDAO i18NDAO;
	private SiteAggregationMenuDAO siteAggregationMenuDAO;
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

	public SiteAggregationMenuDAO getSiteAggregationMenuDAO() {
		return siteAggregationMenuDAO;
	}

	public void setSiteAggregationMenuDAO(SiteAggregationMenuDAO siteAggregationMenuDAO) {
		this.siteAggregationMenuDAO = siteAggregationMenuDAO;
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
	public void createSquare(String squareId, String squareName, String desc,  String sourceSquareId, String userId) {
		if(!existsSquare(sourceSquareId)) {
			log.error("Not exist source square.");
			throw new IllegalArgumentException();
		}

		String maxUser = InfoScoopProperties.getInstance().getProperty(SQUARE_MAX_USER_NUMBER);
		if(maxUser == null || maxUser.length() == 0) maxUser = DEFAULT_MAX_USER;

		this.squareDAO.create(squareId, squareName, desc, userId, Integer.parseInt(maxUser));
		this.forbiddenURLDAO.copySquare(squareId, sourceSquareId);
		this.gadgetDAO.copySquare(squareId, sourceSquareId);
		this.gadgetIconDAO.copySquare(squareId, sourceSquareId);
		this.holidaysDAO.copySquare(squareId, sourceSquareId);
		this.i18NDAO.copySquare(squareId, sourceSquareId);
		this.siteAggregationMenuDAO.copySquare(squareId, sourceSquareId);
		this.oauthCertificateDAO.copySquare(squareId, sourceSquareId);
		this.portalLayoutDAO.copySquare(squareId, sourceSquareId);
		this.propertiesDAO.copySquare(squareId, sourceSquareId);
		this.proxyConfDAO.copySquare(squareId, sourceSquareId);
		this.searchEngineDAO.copySquare(squareId, sourceSquareId);
		this.tabLayoutDAO.copySquare(squareId, sourceSquareId);
		this.widgetConfDAO.copySquare(squareId, sourceSquareId);
		this.staticTabDAO.copySquare(squareId, sourceSquareId);
		this.oauth2ProviderClientDetailDAO.copySquare(squareId, sourceSquareId);

		// copy Adminrole
		List<Adminrole> adminRoleList = adminRoleDAO.select(sourceSquareId);
		Iterator<Adminrole> rolesIte = adminRoleList.iterator();
		String squareAdminRoleId = null;
		while(rolesIte.hasNext()){
			Adminrole adminRole = rolesIte.next();
			String newId = adminRoleDAO.insert(adminRole.getRoleid(), adminRole.getName(), adminRole.getPermission(), adminRole.isAllowDelete(), squareId, new Boolean(true));
			if(SQUARE_ADMIN_ROLE_NAME.equals(adminRole.getRoleid()))
				squareAdminRoleId = newId;
		}

		// add Square Adminirstrator
		portalAdminsDAO.insert(userId, new Integer(squareAdminRoleId), squareId);
	}

	public Map<String, Object> getBelongSquaresNames(String userId, String currentSquareId) throws Exception{
		IAccount account = AuthenticationService.getInstance().getAccountManager().getUser(userId);
		List<Square> squares = squareDAO.getSquares(account.getBelongids());
		List<Map<String, String>> belongSquaresName = new ArrayList<Map<String, String>>();
		Map<String, Object> squareNameMap = new HashMap<String, Object>();

		for(Iterator<Square> itr = squares.iterator();itr.hasNext();) {
			Square square = itr.next();
			Map<String, String> map = new HashMap<String, String>();
			map.put("id", square.getId());
			map.put("name", square.getName());
			if(currentSquareId.equals(square.getId())){
				squareNameMap.put("current", map);
			}else {
				belongSquaresName.add(map);
			}
		}

		squareNameMap.put("belong", belongSquaresName);

		return squareNameMap;
	}

	public Square getEntity(String squareId) {
		return squareDAO.get(squareId);
	}

	public String getSquareName(String squareId) {
		Square sq = squareDAO.get(squareId);
		return sq.getName();
	}

	public boolean existsSquare(String squareId) {
		boolean result = false;
		if(squareDAO.get(squareId) != null) {
			result = true;
		}

		return result;
	}
	
	public void updateSquare(String squareId, String name, String description){
		Square square = squareDAO.get(squareId);
		square.setName(name);
		square.setDescription(description);
		square.setLastmodified(new Date());
		squareDAO.update(square);
	}

	public void deleteSquare(String squareId){
		Square square = squareDAO.get(squareId);
		squareDAO.delete(square);
	}

	public void deleteOwnerSquare(String userId) {
		squareDAO.deleteByOwner(userId);
	}
}
