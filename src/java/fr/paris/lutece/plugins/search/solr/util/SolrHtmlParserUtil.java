/*
 * Copyright (c) 2002-2025, City of Paris
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

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import org.jsoup.Jsoup;

import fr.paris.lutece.plugins.search.solr.indexer.SolrItem;

/**
 * Util class for parsing HTML content.
 */
public final class SolrHtmlParserUtil
{
    private SolrHtmlParserUtil( )
    {
    }

    /**
     * Parse the xml content
     * 
     * @param strContentToIndex
     * @return the parsed content
     * @throws LuteceSolrException
     */
    public static String parseHtml( String strContentToIndex )
    {  	
        return Jsoup.parse( strContentToIndex ).text( );                   
    }

    /**
     * Identify the type of content and parse the stream
     * 
     * @param stream
     * @return the string
     * @throws LuteceSolrException
     */
    public static String parse( InputStream stream ) throws LuteceSolrException
    {
    	try
    	{
    		String content = new String( stream.readAllBytes(), StandardCharsets.UTF_8 );
    		return Jsoup.parse( content ).text( ); 
    	}
    	catch( IOException e )
    	{
    		throw new LuteceSolrException( "Error parsing content", e );
    	} 	
    }

    /**
     * Parse and add the content of a file to the solr item.
     * 
     * @param item
     * @param fileContent
     *            the content of the file
     * @throws LuteceSolrException
     */
    public static void addFileContentToSolrItem( SolrItem item, byte [ ] fileContent )
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
    public static void addFileContentToSolrItem( SolrItem item, List<byte [ ]> fileContentList )
    {
        StringBuilder content = new StringBuilder( );
        for ( byte [ ] fileContent : fileContentList )
        {
            content.append( " " );
            content.append( Jsoup.parse( new String( fileContent, StandardCharsets.UTF_8 ) ).text( ) );
            
        }
        item.setFileContent( content.toString( ) );
    }
}
