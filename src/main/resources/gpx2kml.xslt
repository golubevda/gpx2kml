<?xml version="1.0"?>
<xsl:stylesheet version="3.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="xml" encoding="UTF-8"/>

    <xsl:param name="docName"/>

    <xsl:template match="/">
        <kml xmlns="http://www.opengis.net/kml/2.2">
            <Document id="caches">
                <name>
                    <xsl:choose>
                        <xsl:when test="$docName">
                            <xsl:value-of select="$docName"/>
                        </xsl:when>
                        <xsl:otherwise>Тайники</xsl:otherwise>
                    </xsl:choose>
                </name>
                <Folder>
                    <name>Caches</name>
                    <xsl:for-each
                            select="*[local-name()='gpx']/*[local-name()='wpt'][starts-with(*[local-name()='type'], 'Geocache|')]">
                        <xsl:call-template name="placeMarkCache"/>
                    </xsl:for-each>
                </Folder>
                <Folder>
                    <name>Waypoints</name>
                    <xsl:for-each
                            select="*[local-name()='gpx']/*[local-name()='wpt'][starts-with(*[local-name()='type'], 'Waypoint|')]">
                        <xsl:call-template name="placeMarkWaypoint"/>
                    </xsl:for-each>
                </Folder>
            </Document>
        </kml>
    </xsl:template>

    <xsl:template name="placeMarkCache">
        <Placemark>
            <name>
                <xsl:value-of select="*[local-name()='desc']"/>
            </name>
            <description>
                <xsl:call-template name="description"/>
            </description>
            <Point>
                <coordinates>
                    <xsl:value-of select="@lon"/>,<xsl:value-of select="@lat"/>
                </coordinates>
            </Point>
        </Placemark>
    </xsl:template>

    <xsl:template name="placeMarkWaypoint">
        <Placemark>
            <name>
                <xsl:value-of select="*[local-name()='name']"/>
            </name>
            <description>
                <xsl:value-of select="*[local-name()='sym']"/>
            </description>
            <Point>
                <coordinates>
                    <xsl:value-of select="@lon"/>,<xsl:value-of select="@lat"/>
                </coordinates>
            </Point>
        </Placemark>
    </xsl:template>

    <xsl:template name="description">
        <xsl:variable name="cacheCode" select="*[local-name()='name']"/>
        <xsl:variable name="cacheDate" select="*[local-name()='time']"/>
        &lt;a href=&quot;<xsl:value-of select="(*[local-name()='url'])"/>&quot;&gt;[<xsl:value-of select="$cacheCode"/>]
        <xsl:value-of select="*[local-name()='desc']"/>&lt;/a&gt;
        &lt;p&gt;
        <xsl:for-each select="*[local-name()='cache']">
            <xsl:call-template name="descHeader">
                <xsl:with-param name="cacheCode" select="$cacheCode"/>
                <xsl:with-param name="cacheDate" select="$cacheDate"/>
            </xsl:call-template>
        </xsl:for-each>
        &lt;/p&gt;
        &lt;p&gt;
        <xsl:call-template name="imgToLinks">
            <xsl:with-param name="text" select="*[local-name()='cache']/*[local-name()='long_description']"/>
        </xsl:call-template>
        &lt;/p&gt;
        &lt;p&gt;
        &lt;b&gt;Записи в блокноте тайника&lt;/b&gt;
        &lt;/p&gt;
        <xsl:for-each select="*[local-name()='cache']">
            <xsl:call-template name="logs"/>
        </xsl:for-each>
    </xsl:template>

    <xsl:template name="logs">
        <xsl:choose>
            <xsl:when  test="count(*[local-name()='logs']/*[local-name()='log']) > 0">
                &lt;style&gt;
                .log { border: 1px solid; padding: 4px; margin: 4px 0; border-radius: 6px }
                .log > div { display: table; width: 100% }
                .log > div > div { display: table-cell }
                .log > div:nth-of-type(1) > div:nth-of-type(2) { text-align: right }
                .log .log-type { font-weight: bold }
                &lt;/style&gt;
                <xsl:for-each select="*[local-name()='logs']/*[local-name()='log']">
                    &lt;div class=&quot;log&quot;&gt;
                        &lt;div&gt;
                            &lt;div&gt;
                                <xsl:call-template name="outputLogType">
                                    <xsl:with-param name="type" select="*[local-name()='type']"/>
                                </xsl:call-template>
                                <xsl:text> </xsl:text>&lt;b&gt;<xsl:value-of select="*[local-name()='finder']"/>&lt;/b&gt;
                            &lt;/div&gt;
                            &lt;div&gt;
                            <xsl:call-template name="dateTime">
                                <xsl:with-param name="value" select="*[local-name()='date']"/>
                            </xsl:call-template>
                            &lt;/div&gt;
                        &lt;/div&gt;
                        &lt;div&gt;
                            &lt;div&gt;
                                <xsl:value-of select="*[local-name()='text']"/>
                            &lt;/div&gt;
                        &lt;/div&gt;
                    &lt;/div&gt;
                </xsl:for-each>
            </xsl:when>
            <xsl:otherwise>
                Записи отсутствуют
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template name="descHeader">
        <xsl:param name="cacheCode"/>
        <xsl:param name="cacheDate"/>
        &lt;style&gt;
        .green { color: green }
        .peru { color: peru }
        .red { color: red }
        .grey { color: grey }
        .blue { color: blue }
        .skyblue { color: skyblue }
        .orange { color: orange }
        .saddlebrown { color: saddlebrown }
        .yellow { color: yellow }
        .cache-type { font-weight: bold }
        &lt;/style&gt;
        &lt;b&gt;Тип:&lt;/b&gt;<xsl:text> </xsl:text>
        <xsl:call-template name="outputCacheType">
            <xsl:with-param name="code" select="$cacheCode"/>
            <xsl:with-param name="type" select="*[local-name()='type']"/>
        </xsl:call-template>
        &lt;br/&gt;
        &lt;b&gt;Создан:&lt;/b&gt;
        <xsl:call-template name="dateOnly">
            <xsl:with-param name="value" select="$cacheDate"/>
        </xsl:call-template>
        &lt;br/&gt;
        &lt;b&gt;Автор:&lt;/b&gt; <xsl:value-of select="*[local-name()='owner']"/>&lt;br/&gt;
        &lt;b&gt;Доступность:&lt;/b&gt;
        <xsl:value-of select="*[local-name()='difficulty']"/> из 5&lt;br/&gt;
        &lt;b&gt;Местность:&lt;/b&gt;
        <xsl:value-of select="*[local-name()='terrain']"/> из 5&lt;br/&gt;
        &lt;b&gt;Контейнер:&lt;/b&gt; <xsl:value-of select="*[local-name()='container']"/>&lt;br/&gt;
        <xsl:if test="count(*[local-name()='attributes']/*[local-name()='attribute'])>0">
            &lt;b&gt;Атрибуты:&lt;/b&gt;
            <xsl:for-each select="*[local-name()='attributes']">
                <xsl:call-template name="attributes"/>
            </xsl:for-each>
        </xsl:if>
    </xsl:template>

    <xsl:template name="attributes">
        <xsl:for-each select="*[local-name()='attribute']">
            <xsl:value-of select="."/>
            <xsl:if test="position() != last()">
                <xsl:text>, </xsl:text>
            </xsl:if>
        </xsl:for-each>
    </xsl:template>

    <xsl:template name="dateTime">
        <xsl:param name="value"/>
        <xsl:call-template name="dateOnly">
            <xsl:with-param name="value" select="$value"/>
        </xsl:call-template>
        <xsl:text> </xsl:text>
        <xsl:variable name="rawTime" select="substring-after($value, 'T')"/>
        <xsl:variable name="hour" select="substring-before($rawTime, ':')"/>
        <xsl:variable name="minute" select="substring-before(substring-after($rawTime, ':'), ':')"/>
        <xsl:variable name="second" select="substring-after(substring-after($rawTime, ':'), ':')"/>
        <xsl:value-of select="$hour"/>:<xsl:value-of select="$minute"/>:<xsl:value-of select="$second"/>
    </xsl:template>

    <xsl:template name="dateOnly">
        <xsl:param name="value"/>
        <xsl:variable name="rawDate" select="substring-before($value, 'T')"/>
        <xsl:variable name="year" select="substring-before($rawDate, '-')"/>
        <xsl:variable name="month" select="substring-before(substring-after($rawDate, '-'), '-')"/>
        <xsl:variable name="day" select="substring-after(substring-after($rawDate, '-'), '-')"/>
        <xsl:value-of select="$day"/>.<xsl:value-of select="$month"/>.<xsl:value-of select="$year"/>
    </xsl:template>

    <xsl:template name="imgToLinks">
        <xsl:param name="text"/>
        <xsl:value-of
                select="replace($text, '&lt;\s*img.*?src=&quot;(.*?/photos/.*?)&quot;.*?\s+alt=&quot;(.*?)&quot;.*?&gt;', '&lt;p&gt;&lt;a href=&quot;$1&quot;&gt;[ФОТО] $2&lt;/a&gt;&lt;/p&gt;', 's')"/>
    </xsl:template>

    <xsl:template name="outputLogType">
        <xsl:param name="type"/>
        <xsl:choose>
            <xsl:when test="$type='Found it'">
                <xsl:call-template name="logTypeSpan">
                    <xsl:with-param name="color" select="'green'"/>
                    <xsl:with-param name="label" select="'&#10004;'"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:when test="$type='Restored'">
                <xsl:call-template name="logTypeSpan">
                    <xsl:with-param name="color" select="'green'"/>
                    <xsl:with-param name="label" select="'&#9873;'"/>
                </xsl:call-template>
            </xsl:when>
                <xsl:when test="$type='Creator''s Check'">
                    <xsl:call-template name="logTypeSpan">
                    <xsl:with-param name="color" select="'green'"/>
                    <xsl:with-param name="label" select="'&#128076;'"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:when test="$type='Visited'">
                <xsl:call-template name="logTypeSpan">
                    <xsl:with-param name="color" select="'grey'"/>
                    <xsl:with-param name="label" select="'&#128099;'"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:when test="$type='Write note'">
                <xsl:call-template name="logTypeSpan">
                    <xsl:with-param name="color" select="'grey'"/>
                    <xsl:with-param name="label" select="'&#128393;'"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:when test="$type='Didn''t find it'">
                <xsl:call-template name="logTypeSpan">
                    <xsl:with-param name="color" select="'red'"/>
                    <xsl:with-param name="label" select="'&#10006;'"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
                <xsl:call-template name="logTypeSpan">
                    <xsl:with-param name="color" select="'red'"/>
                    <xsl:with-param name="label" select="concat('[', $type, ']')"/>
                </xsl:call-template>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template name="logTypeSpan">
        <xsl:param name="color"/>
        <xsl:param name="label"/>
        &lt;span class="log-type <xsl:value-of select="$color"/>"&gt;<xsl:value-of select="$label"/>&lt;/span&gt;
    </xsl:template>

    <xsl:template name="outputCacheType">
        <xsl:param name="code"/>
        <xsl:param name="type"/>
        <xsl:choose>
            <xsl:when test="starts-with($code, 'TR')">
                <xsl:call-template name="cacheTypeSpan">
                    <xsl:with-param name="color" select="'peru'"/>
                    <xsl:with-param name="label" select="'Традиционный'"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:when test="starts-with($code, 'MS')">
                <xsl:call-template name="cacheTypeSpan">
                    <xsl:with-param name="color" select="'green'"/>
                    <xsl:with-param name="label" select="'Пошаговый традиционный'"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:when test="starts-with($code, 'LT')">
                <xsl:call-template name="cacheTypeSpan">
                    <xsl:with-param name="color" select="'blue'"/>
                    <xsl:with-param name="label" select="'Логический традиционный'"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:when test="starts-with($code, 'VI')">
                <xsl:call-template name="cacheTypeSpan">
                    <xsl:with-param name="color" select="'orange'"/>
                    <xsl:with-param name="label" select="'Виртуальный'"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:when test="starts-with($code, 'MV')">
                <xsl:call-template name="cacheTypeSpan">
                    <xsl:with-param name="color" select="'grey'"/>
                    <xsl:with-param name="label" select="'Пошаговый виртуальный'"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:when test="starts-with($code, 'LV')">
                <xsl:call-template name="cacheTypeSpan">
                    <xsl:with-param name="color" select="'saddlebrown'"/>
                    <xsl:with-param name="label" select="'Логический виртуальный'"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:when test="starts-with($code, 'EV')">
                <xsl:call-template name="cacheTypeSpan">
                    <xsl:with-param name="color" select="'skyblue'"/>
                    <xsl:with-param name="label" select="'Встреча'"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:when test="starts-with($code, 'CT')">
                <xsl:call-template name="cacheTypeSpan">
                    <xsl:with-param name="color" select="'yellow'"/>
                    <xsl:with-param name="label" select="'Конкрус'"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
                <xsl:call-template name="cacheTypeSpan">
                    <xsl:with-param name="color" select="'red'"/>
                    <xsl:with-param name="label" select="$type"/>
                </xsl:call-template>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template name="cacheTypeSpan">
        <xsl:param name="color"/>
        <xsl:param name="label"/>
        &lt;span class="cache-type <xsl:value-of select="$color"/>"&gt;<xsl:value-of select="$label"/>&lt;/span&gt;
    </xsl:template>

</xsl:stylesheet>