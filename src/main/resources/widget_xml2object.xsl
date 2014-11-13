<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">

	<xsl:output method="xml" encoding="UTF-8" indent="yes"/>
	
	<xsl:template match="/widgets">
		<xsl:variable name="colnum" select="@column"/>
		<xsl:variable name="widgetId" select="@id"/>
		<xsl:element name="widgets">
			<xsl:for-each select="widget">
			<xsl:element name="widget">
				<xsl:attribute name="widgetId"><xsl:value-of select="@id" /></xsl:attribute>
				<xsl:attribute name="menuId"><xsl:value-of select="@menuId" /></xsl:attribute>
				<xsl:attribute name="colnum"><xsl:value-of select="@column" /></xsl:attribute>
				<xsl:attribute name="siblingId"><xsl:value-of select="./preceding-sibling::widget[1][@column=$colnum]/@id" /></xsl:attribute>
				<xsl:attribute name="href"><xsl:value-of select="@href" /></xsl:attribute>
				<xsl:attribute name="title"><xsl:value-of select="@title" /></xsl:attribute>
				<xsl:attribute name="type"><xsl:value-of select="@type"/></xsl:attribute>
                <xsl:attribute name="refreshInterval"><xsl:value-of select="@refreshInterval"/></xsl:attribute>
				<xsl:if test="@ignoreHeader='true'">
					<xsl:attribute name="ignoreHeader"><xsl:value-of select="@ignoreHeader"/></xsl:attribute>
				</xsl:if>
				<xsl:if test="@noBorder='true'">
					<xsl:attribute name="noBorder"><xsl:value-of select="@noBorder"/></xsl:attribute>
				</xsl:if>
				<xsl:element name="data"><xsl:copy-of select="data/property"/></xsl:element>
			</xsl:element>
			<xsl:for-each select="data/feed">
				<xsl:if test="@isChecked='true'">
				<xsl:element name="widget">
					<xsl:attribute name="widgetId"><xsl:value-of select="@id" /></xsl:attribute>
					<xsl:attribute name="siblingId"><xsl:value-of select="./preceding-sibling::feed[1]/@id" /></xsl:attribute>
					<xsl:attribute name="parentId"><xsl:value-of select="$widgetId" /></xsl:attribute>
					<xsl:attribute name="href"><xsl:value-of select="@href" /></xsl:attribute>
					<xsl:attribute name="title"><xsl:value-of select="@title" /></xsl:attribute>
					<xsl:attribute name="type"><xsl:value-of select="@type"/></xsl:attribute>
					<xsl:element name="data"><xsl:copy-of select="data/property"/></xsl:element>
					<xsl:copy-of select="data/property"/>
				</xsl:element>
				</xsl:if>
			</xsl:for-each>
			</xsl:for-each>
		</xsl:element>
	</xsl:template>

</xsl:stylesheet>
