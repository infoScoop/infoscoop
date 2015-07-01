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

import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.account.AuthenticationService;
import org.infoscoop.account.IAccount;
import org.infoscoop.api.dao.OAuth2ProviderClientDetailDAO;
import org.infoscoop.dao.*;
import org.infoscoop.dao.model.Adminrole;
import org.infoscoop.dao.model.Square;
import org.infoscoop.util.SpringUtil;

public class SquareService {
	private static Log log = LogFactory.getLog(SquareService.class);
	public static final String SQUARE_ADMIN_ROLE_NAME = "squareAdmin";

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

		this.squareDAO.create(squareId, squareName, desc);
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

//		Map<Integer, Integer> roleIdMap = new HashMap<Integer, Integer>();
		// copy Adminrole
		List<Adminrole> adminRoleList = adminRoleDAO.select(sourceSquareId);
		Iterator<Adminrole> rolesIte = adminRoleList.iterator();
		String squareAdminRoleId = null;
		while(rolesIte.hasNext()){
			Adminrole adminRole = rolesIte.next();
//			String orgId = adminRole.getId();
			String newId = adminRoleDAO.insert(adminRole.getRoleid(), adminRole.getName(), adminRole.getPermission(), adminRole.isAllowDelete(), squareId, new Boolean(true));
			if(SQUARE_ADMIN_ROLE_NAME.equals(adminRole.getRoleid()))
				squareAdminRoleId = newId;
//			roleIdMap.put(new Integer(orgId), new Integer(newId));
		}
		
		// copy PortalAdmins
//		List<Portaladmins> portalAdminList = portalAdminsDAO.select(sourceSquareId);
//		Iterator<Portaladmins> adminsIte = portalAdminList.iterator();
//		while(adminsIte.hasNext()){
//			Portaladmins portalAdmin = adminsIte.next();
//			Integer orgRoleId = portalAdmin.getRoleid();
//			portalAdminsDAO.insert(portalAdmin.getUid(), roleIdMap.get(orgRoleId), squareId);
//		}

		// add Square Adminirstrator
		portalAdminsDAO.insert(userId, new Integer(squareAdminRoleId), squareId);
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> getBelongSquaresNames(String userId, String currentSquareId) throws Exception{
		IAccount account = AuthenticationService.getInstance().getAccountManager().getUser(userId);
		List<Square> squares = squareDAO.getSquares(account.getBelongid());
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

		if(belongSquaresName.size() > 0)
			squareNameMap.put("belong", belongSquaresName);

		return squareNameMap;
	}

	@SuppressWarnings("unchecked")
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
}
