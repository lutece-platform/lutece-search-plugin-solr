/*
 * Copyright (c) 2002-2020, City of Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.search.solr.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import org.apache.tika.config.TikaConfig;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.html.HtmlParser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import fr.paris.lutece.plugins.search.solr.indexer.SolrItem;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

/**
 * Util class for use of tika methods.
 */
public final class TikaIndexerUtil
{
    // 1Mo
    private static final int DEFAULT_DOCUMENT_SIZE = 1048576;
    private static final String CONFIG = AppPathService.getAbsolutePathFromRelativePath( "/WEB-INF/conf/tika.xml" );
    
    private TikaIndexerUtil( )
    {
    }
    
    /**
     * Parse the xml content
     * 
     * @param strContentToIndex
     * @param metadata
     * @param parseContext
     * @return the content handler containing the parsed content
     * @throws LuteceSolrException
     */
    public static ContentHandler parseHtml( String strContentToIndex, Metadata metadata, ParseContext parseContext ) throws LuteceSolrException
    {
        try
        {
            ContentHandler handler = new BodyContentHandler( AppPropertiesService.getPropertyInt( "solr.document.max.size", DEFAULT_DOCUMENT_SIZE ) );
            new HtmlParser( ).parse( new ByteArrayInputStream( strContentToIndex.getBytes( ) ), handler, metadata, parseContext );
            return handler;
        }
        catch( IOException | SAXException | TikaException e )
        {
            throw new LuteceSolrException( "Error parsing content", e );
        }
    }

    /**
     * Identify the type of content and parse the stream
     * 
     * @param stream
     * @return the content handler containing the parsed content
     * @throws LuteceSolrException
     */
    public static ContentHandler parse( InputStream stream, Metadata metadata, ParseContext parseContext ) throws LuteceSolrException
    {
        try
        {
            ContentHandler handler = new BodyContentHandler(  );
            AutoDetectParser parser = new AutoDetectParser( new TikaConfig( CONFIG ) );
            parser.parse( stream, handler, metadata, parseContext );
            return handler;
        }
        catch( IOException | SAXException | TikaException e )
        {
            throw new LuteceSolrException( "Error parsing content", e );
        }
    }

    /**
     * Identify the type of content and parse the stream
     * 
     * @param stream
     * @return the content handler containing the parsed content
     * @throws LuteceSolrException
     */
    public static ContentHandler parse( InputStream stream ) throws LuteceSolrException
    {
        return parse( stream, new Metadata( ), new ParseContext( ) );
    }

    /**
     * Parse the xml content
     * 
     * @param strContentToIndex
     * @return the content handler containing the parsed content
     * @throws LuteceSolrException
     */
    public static ContentHandler parseHtml( String strContentToIndex ) throws LuteceSolrException
    {
        return parseHtml( strContentToIndex, new Metadata( ), new ParseContext( ) );
    }

    /**
     * Parse and add the content of a file to the solr item.
     * 
     * @param item
     * @param fileContent
     *            the content of the file
     * @throws LuteceSolrException
     */
    public static void addFileContentToSolrItem( SolrItem item, byte [ ] fileContent ) throws LuteceSolrException
    {
        addFileContentToSolrItem( item, Collections.singletonList( fileContent ) );
    }

    /**
     * Parse and add the content of multiples files to the solr item.
     * 
     * @param item
     * @param fileContentList
     *            the content of the files
     * @throws LuteceSolrException
     */
    public static void addFileContentToSolrItem( SolrItem item, List<byte [ ]> fileContentList ) throws LuteceSolrException
    {
        StringBuilder content = new StringBuilder( );
        for ( byte [ ] fileContent : fileContentList )
        {
            content.append( " " );
            try ( InputStream bais = new ByteArrayInputStream( fileContent ) )
            {
                ContentHandler handler = parse( bais );
                content.append( handler.toString( ) );
            }
            catch (IOException e)
            {
                throw new LuteceSolrException( "Error while parsing file for item " + item.getUid( ), e );
            }
        }
        item.setFileContent( content.toString( ) );
    }
}
