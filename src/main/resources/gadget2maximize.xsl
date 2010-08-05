<?xml version="1.0" encoding="utf-8"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
	<xsl:output method="xml" encoding="utf-8" cdata-section-elements="Content"/>
	<xsl:param name="type"/>
	<xsl:template match="Module">
		<Module>
			<xsl:apply-templates select="*"/>
		</Module>
	</xsl:template>
	<xsl:template match="*">
		<xsl:copy-of select="."/>
	</xsl:template>
	<xsl:template match="Header">
		<xsl:choose><xsl:when test="/Module/Maximize/Header">
				<xsl:copy-of select="/Module/Maximize/Header"/>
		</xsl:when><xsl:otherwise>
				<xsl:copy-of select="."/>
		</xsl:otherwise></xsl:choose>
	</xsl:template>
	<xsl:template match="Content">
		<xsl:choose><xsl:when test="/Module/Maximize/Content">
			<xsl:copy-of select="/Module/Maximize/Content"/>
		</xsl:when><xsl:otherwise>
			<xsl:copy-of select="."/>
		</xsl:otherwise></xsl:choose>
	</xsl:template>
	<xsl:template match="Maximize">
		<xsl:if test="not(/Module/Header)">
			<xsl:copy-of select="Header"/>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>