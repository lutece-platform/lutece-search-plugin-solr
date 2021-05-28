/*
 * Copyright (c) 2002-2021, City of Paris
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
package fr.paris.lutece.plugins.search.solr.business.field;

/**
 * This is the business class for the object Field
 */
public class Field
{
    // Variables declarations
    public static final String OPERATOR_TYPE_OR = "OR";
    public static final String OPERATOR_TYPE_SWITCH = "SWITCH";
    public static final String OPERATOR_TYPE_AND = "AND";

    private int _nIdField;
    private String _strName;
    private String _strLabel;
    private String _strDescription;
    private boolean _isFacet;
    private boolean _enableFacet;
    private boolean _isSort;
    private boolean _enableSort;
    private boolean _defaultSort;
    private double _weight;
    private String _strOperator;
    private int _facetMincount;

    public Field( )
    {
        this._strName = "";
        this._strLabel = "";
        this._strDescription = "";
        this._isFacet = false;
        this._enableFacet = false;
        this._isSort = false;
        this._enableSort = false;
        this._defaultSort = false;
        this._facetMincount = 1;
        this._strOperator = OPERATOR_TYPE_AND;
    }

    public String getSolrName( )
    {
        return this._strName;
    }

    /**
     * Returns the IdField
     * 
     * @return The IdField
     */
    public int getIdField( )
    {
        return _nIdField;
    }

    /**
     * Sets the IdField
     * 
     * @param nIdField
     *            The IdField
     */
    public void setIdField( int nIdField )
    {
        _nIdField = nIdField;
    }

    /**
     * Returns the Name
     * 
     * @return The Name
     */
    public String getName( )
    {
        return _strName;
    }

    /**
     * Sets the Name
     * 
     * @param strName
     *            The Name
     */
    public void setName( String strName )
    {
        _strName = strName;
    }

    /**
     * Returns the Label
     * 
     * @return The Label
     */
    public String getLabel( )
    {
        return _strLabel;
    }

    /**
     * Sets the Label
     * 
     * @param strLabel
     *            The Label
     */
    public void setLabel( String strLabel )
    {
        _strLabel = strLabel;
    }

    /**
     * Returns the Description
     * 
     * @return The Description
     */
    public String getDescription( )
    {
        return _strDescription;
    }

    /**
     * Sets the Description
     * 
     * @param strDescription
     *            The Description
     */
    public void setDescription( String strDescription )
    {
        _strDescription = strDescription;
    }

    /**
     * Returns the IsFacet
     * 
     * @return The IsFacet
     */
    public boolean getIsFacet( )
    {
        return _isFacet;
    }

    /**
     * Sets the IsFacet
     * 
     * @param isFacet
     *            The IsFacet
     */
    public void setIsFacet( boolean isFacet )
    {
        _isFacet = isFacet;
    }

    /**
     * Returns the EnableFacet
     * 
     * @return The EnableFacet
     */
    public boolean getEnableFacet( )
    {
        return _enableFacet;
    }

    /**
     * Sets the EnableFacet
     * 
     * @param enableFacet
     *            The EnableFacet
     */
    public void setEnableFacet( boolean enableFacet )
    {
        _enableFacet = enableFacet;
    }

    /**
     * Returns the IsSort
     * 
     * @return The IsSort
     */
    public boolean getIsSort( )
    {
        return _isSort;
    }

    /**
     * Sets the IsSort
     * 
     * @param isSort
     *            The IsSort
     */
    public void setIsSort( boolean isSort )
    {
        _isSort = isSort;
    }

    /**
     * Returns the EnableSort
     * 
     * @return The EnableSort
     */
    public boolean getEnableSort( )
    {
        return _enableSort;
    }

    /**
     * Sets the EnableSort
     * 
     * @param enableSort
     *            The EnableSort
     */
    public void setEnableSort( boolean enableSort )
    {
        _enableSort = enableSort;
    }

    /**
     * Returns the DefaultSort
     * 
     * @return The DefaultSort
     */
    public boolean getDefaultSort( )
    {
        return _defaultSort;
    }

    /**
     * Sets the DefaultSort
     * 
     * @param defaultSort
     *            The DefaultSort
     */
    public void setDefaultSort( boolean defaultSort )
    {
        _defaultSort = defaultSort;
    }

    /**
     * Sets the weight
     * 
     * @param Weight
     *            The Weight
     */
    public void setWeight( double weight )
    {
        _weight = weight;
    }

    /**
     * Returns the weight
     * 
     * @return The weight
     */
    public double getWeight( )
    {
        return _weight;
    }

    /**
     * Returns the FacetteMincount
     * 
     * @return The FacetteMincount
     */
    public int getFacetMincount( )
    {
        return _facetMincount;
    }

    /**
     * Returns the FacetMincount
     * 
     * @return The FacetteMincount
     */
    public void setFacetMincount( int facetMincount )
    {
        this._facetMincount = facetMincount;
    }

    /**
     * Return operator
     * 
     * @return The Operator
     */
    public String getOperator( )
    {
        return _strOperator;
    }

    /**
     * Set the operator
     * 
     * @param strOperator
     */
    public void setOperator( String strOperator )
    {
        strOperator = !( OPERATOR_TYPE_SWITCH.equalsIgnoreCase( strOperator ) || OPERATOR_TYPE_OR.equalsIgnoreCase( strOperator ) ) ? OPERATOR_TYPE_AND
                : strOperator;
        this._facetMincount = OPERATOR_TYPE_AND.equalsIgnoreCase( strOperator ) ? 1 : 0;
        this._strOperator = strOperator;
    }
}
