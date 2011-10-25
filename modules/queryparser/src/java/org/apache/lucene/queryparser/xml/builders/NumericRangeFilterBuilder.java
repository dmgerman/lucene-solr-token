begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.queryparser.xml.builders
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryparser
operator|.
name|xml
operator|.
name|builders
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|IndexReader
operator|.
name|AtomicReaderContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|DocIdSet
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|Filter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|NumericRangeFilter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|Bits
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|NumericUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryparser
operator|.
name|xml
operator|.
name|DOMUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryparser
operator|.
name|xml
operator|.
name|FilterBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryparser
operator|.
name|xml
operator|.
name|ParserException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Element
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * Creates a {@link NumericRangeFilter}. The table below specifies the required  * attributes and the defaults if optional attributes are omitted. For more  * detail on what each of the attributes actually do, consult the documentation  * for {@link NumericRangeFilter}:  *<table>  *<tr>  *<th>Attribute name</th>  *<th>Values</th>  *<th>Required</th>  *<th>Default</th>  *</tr>  *<tr>  *<td>fieldName</td>  *<td>String</td>  *<td>Yes</td>  *<td>N/A</td>  *</tr>  *<tr>  *<td>lowerTerm</td>  *<td>Specified by<tt>type</tt></td>  *<td>Yes</td>  *<td>N/A</td>  *</tr>  *<tr>  *<td>upperTerm</td>  *<td>Specified by<tt>type</tt></td>  *<td>Yes</td>  *<td>N/A</td>  *</tr>  *<tr>  *<td>type</td>  *<td>int, long, float, double</td>  *<td>No</td>  *<td>int</td>  *</tr>  *<tr>  *<td>includeLower</td>  *<td>true, false</td>  *<td>No</td>  *<td>true</td>  *</tr>  *<tr>  *<td>includeUpper</td>  *<td>true, false</td>  *<td>No</td>  *<td>true</td>  *</tr>  *<tr>  *<td>precisionStep</td>  *<td>Integer</td>  *<td>No</td>  *<td>4</td>  *</tr>  *</table>  *<p/>  * If an error occurs parsing the supplied<tt>lowerTerm</tt> or  *<tt>upperTerm</tt> into the numeric type specified by<tt>type</tt>, then the  * error will be silently ignored and the resulting filter will not match any  * documents.  */
end_comment

begin_class
DECL|class|NumericRangeFilterBuilder
specifier|public
class|class
name|NumericRangeFilterBuilder
implements|implements
name|FilterBuilder
block|{
DECL|field|NO_MATCH_FILTER
specifier|private
specifier|static
specifier|final
name|NoMatchFilter
name|NO_MATCH_FILTER
init|=
operator|new
name|NoMatchFilter
argument_list|()
decl_stmt|;
DECL|field|strictMode
specifier|private
name|boolean
name|strictMode
init|=
literal|false
decl_stmt|;
comment|/**    * Specifies how this {@link NumericRangeFilterBuilder} will handle errors.    *<p/>    * If this is set to true, {@link #getFilter(Element)} will throw a    * {@link ParserException} if it is unable to parse the lowerTerm or upperTerm    * into the appropriate numeric type. If this is set to false, then this    * exception will be silently ignored and the resulting filter will not match    * any documents.    *<p/>    * Defaults to false.    *    * @param strictMode    */
DECL|method|setStrictMode
specifier|public
name|void
name|setStrictMode
parameter_list|(
name|boolean
name|strictMode
parameter_list|)
block|{
name|this
operator|.
name|strictMode
operator|=
name|strictMode
expr_stmt|;
block|}
DECL|method|getFilter
specifier|public
name|Filter
name|getFilter
parameter_list|(
name|Element
name|e
parameter_list|)
throws|throws
name|ParserException
block|{
name|String
name|field
init|=
name|DOMUtils
operator|.
name|getAttributeWithInheritanceOrFail
argument_list|(
name|e
argument_list|,
literal|"fieldName"
argument_list|)
decl_stmt|;
name|String
name|lowerTerm
init|=
name|DOMUtils
operator|.
name|getAttributeOrFail
argument_list|(
name|e
argument_list|,
literal|"lowerTerm"
argument_list|)
decl_stmt|;
name|String
name|upperTerm
init|=
name|DOMUtils
operator|.
name|getAttributeOrFail
argument_list|(
name|e
argument_list|,
literal|"upperTerm"
argument_list|)
decl_stmt|;
name|boolean
name|lowerInclusive
init|=
name|DOMUtils
operator|.
name|getAttribute
argument_list|(
name|e
argument_list|,
literal|"includeLower"
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|boolean
name|upperInclusive
init|=
name|DOMUtils
operator|.
name|getAttribute
argument_list|(
name|e
argument_list|,
literal|"includeUpper"
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|int
name|precisionStep
init|=
name|DOMUtils
operator|.
name|getAttribute
argument_list|(
name|e
argument_list|,
literal|"precisionStep"
argument_list|,
name|NumericUtils
operator|.
name|PRECISION_STEP_DEFAULT
argument_list|)
decl_stmt|;
name|String
name|type
init|=
name|DOMUtils
operator|.
name|getAttribute
argument_list|(
name|e
argument_list|,
literal|"type"
argument_list|,
literal|"int"
argument_list|)
decl_stmt|;
try|try
block|{
name|Filter
name|filter
decl_stmt|;
if|if
condition|(
name|type
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"int"
argument_list|)
condition|)
block|{
name|filter
operator|=
name|NumericRangeFilter
operator|.
name|newIntRange
argument_list|(
name|field
argument_list|,
name|precisionStep
argument_list|,
name|Integer
operator|.
name|valueOf
argument_list|(
name|lowerTerm
argument_list|)
argument_list|,
name|Integer
operator|.
name|valueOf
argument_list|(
name|upperTerm
argument_list|)
argument_list|,
name|lowerInclusive
argument_list|,
name|upperInclusive
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"long"
argument_list|)
condition|)
block|{
name|filter
operator|=
name|NumericRangeFilter
operator|.
name|newLongRange
argument_list|(
name|field
argument_list|,
name|precisionStep
argument_list|,
name|Long
operator|.
name|valueOf
argument_list|(
name|lowerTerm
argument_list|)
argument_list|,
name|Long
operator|.
name|valueOf
argument_list|(
name|upperTerm
argument_list|)
argument_list|,
name|lowerInclusive
argument_list|,
name|upperInclusive
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"double"
argument_list|)
condition|)
block|{
name|filter
operator|=
name|NumericRangeFilter
operator|.
name|newDoubleRange
argument_list|(
name|field
argument_list|,
name|precisionStep
argument_list|,
name|Double
operator|.
name|valueOf
argument_list|(
name|lowerTerm
argument_list|)
argument_list|,
name|Double
operator|.
name|valueOf
argument_list|(
name|upperTerm
argument_list|)
argument_list|,
name|lowerInclusive
argument_list|,
name|upperInclusive
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"float"
argument_list|)
condition|)
block|{
name|filter
operator|=
name|NumericRangeFilter
operator|.
name|newFloatRange
argument_list|(
name|field
argument_list|,
name|precisionStep
argument_list|,
name|Float
operator|.
name|valueOf
argument_list|(
name|lowerTerm
argument_list|)
argument_list|,
name|Float
operator|.
name|valueOf
argument_list|(
name|upperTerm
argument_list|)
argument_list|,
name|lowerInclusive
argument_list|,
name|upperInclusive
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|ParserException
argument_list|(
literal|"type attribute must be one of: [long, int, double, float]"
argument_list|)
throw|;
block|}
return|return
name|filter
return|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|nfe
parameter_list|)
block|{
if|if
condition|(
name|strictMode
condition|)
block|{
throw|throw
operator|new
name|ParserException
argument_list|(
literal|"Could not parse lowerTerm or upperTerm into a number"
argument_list|,
name|nfe
argument_list|)
throw|;
block|}
return|return
name|NO_MATCH_FILTER
return|;
block|}
block|}
DECL|class|NoMatchFilter
specifier|static
class|class
name|NoMatchFilter
extends|extends
name|Filter
block|{
annotation|@
name|Override
DECL|method|getDocIdSet
specifier|public
name|DocIdSet
name|getDocIdSet
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|,
name|Bits
name|acceptDocs
parameter_list|)
throws|throws
name|IOException
block|{
return|return
literal|null
return|;
block|}
block|}
block|}
end_class

end_unit

