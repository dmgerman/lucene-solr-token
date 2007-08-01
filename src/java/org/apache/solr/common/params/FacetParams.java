begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.common.params
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|params
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|SolrException
import|;
end_import

begin_comment
comment|/**  * Facet parameters  */
end_comment

begin_interface
DECL|interface|FacetParams
specifier|public
interface|interface
name|FacetParams
block|{
comment|/**    * Should facet counts be calculated?    */
DECL|field|FACET
specifier|public
specifier|static
specifier|final
name|String
name|FACET
init|=
literal|"facet"
decl_stmt|;
comment|/**    * Any lucene formated queries the user would like to use for    * Facet Constraint Counts (multi-value)    */
DECL|field|FACET_QUERY
specifier|public
specifier|static
specifier|final
name|String
name|FACET_QUERY
init|=
literal|"facet.query"
decl_stmt|;
comment|/**    * Any field whose terms the user wants to enumerate over for    * Facet Constraint Counts (multi-value)    */
DECL|field|FACET_FIELD
specifier|public
specifier|static
specifier|final
name|String
name|FACET_FIELD
init|=
literal|"facet.field"
decl_stmt|;
comment|/**    * The offset into the list of facets.    * Can be overridden on a per field basis.    */
DECL|field|FACET_OFFSET
specifier|public
specifier|static
specifier|final
name|String
name|FACET_OFFSET
init|=
literal|"facet.offset"
decl_stmt|;
comment|/**    * Numeric option indicating the maximum number of facet field counts    * be included in the response for each field - in descending order of count.    * Can be overridden on a per field basis.    */
DECL|field|FACET_LIMIT
specifier|public
specifier|static
specifier|final
name|String
name|FACET_LIMIT
init|=
literal|"facet.limit"
decl_stmt|;
comment|/**    * Numeric option indicating the minimum number of hits before a facet should    * be included in the response.  Can be overridden on a per field basis.    */
DECL|field|FACET_MINCOUNT
specifier|public
specifier|static
specifier|final
name|String
name|FACET_MINCOUNT
init|=
literal|"facet.mincount"
decl_stmt|;
comment|/**    * Boolean option indicating whether facet field counts of "0" should     * be included in the response.  Can be overridden on a per field basis.    */
DECL|field|FACET_ZEROS
specifier|public
specifier|static
specifier|final
name|String
name|FACET_ZEROS
init|=
literal|"facet.zeros"
decl_stmt|;
comment|/**    * Boolean option indicating whether the response should include a     * facet field count for all records which have no value for the     * facet field. Can be overridden on a per field basis.    */
DECL|field|FACET_MISSING
specifier|public
specifier|static
specifier|final
name|String
name|FACET_MISSING
init|=
literal|"facet.missing"
decl_stmt|;
comment|/**    * Boolean option: true causes facets to be sorted    * by the count, false results in natural index order.    */
DECL|field|FACET_SORT
specifier|public
specifier|static
specifier|final
name|String
name|FACET_SORT
init|=
literal|"facet.sort"
decl_stmt|;
comment|/**    * Only return constraints of a facet field with the given prefix.    */
DECL|field|FACET_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|FACET_PREFIX
init|=
literal|"facet.prefix"
decl_stmt|;
comment|/**    * When faceting by enumerating the terms in a field,    * only use the filterCache for terms with a df>= to this parameter.    */
DECL|field|FACET_ENUM_CACHE_MINDF
specifier|public
specifier|static
specifier|final
name|String
name|FACET_ENUM_CACHE_MINDF
init|=
literal|"facet.enum.cache.minDf"
decl_stmt|;
comment|/**    * Any field whose terms the user wants to enumerate over for    * Facet Contraint Counts (multi-value)    */
DECL|field|FACET_DATE
specifier|public
specifier|static
specifier|final
name|String
name|FACET_DATE
init|=
literal|"facet.date"
decl_stmt|;
comment|/**    * Date string indicating the starting point for a date facet range.    * Can be overriden on a per field basis.    */
DECL|field|FACET_DATE_START
specifier|public
specifier|static
specifier|final
name|String
name|FACET_DATE_START
init|=
literal|"facet.date.start"
decl_stmt|;
comment|/**    * Date string indicating the endinging point for a date facet range.    * Can be overriden on a per field basis.    */
DECL|field|FACET_DATE_END
specifier|public
specifier|static
specifier|final
name|String
name|FACET_DATE_END
init|=
literal|"facet.date.end"
decl_stmt|;
comment|/**    * Date Math string indicating the interval of sub-ranges for a date    * facet range.    * Can be overriden on a per field basis.    */
DECL|field|FACET_DATE_GAP
specifier|public
specifier|static
specifier|final
name|String
name|FACET_DATE_GAP
init|=
literal|"facet.date.gap"
decl_stmt|;
comment|/**    * Boolean indicating how counts should be computed if the range    * between 'start' and 'end' is not evenly divisible by 'gap'.  If    * this value is true, then all counts of ranges involving the 'end'    * point will use the exact endpoint specified -- this includes the    * 'between' and 'after' counts as well as the last range computed    * using the 'gap'.  If the value is false, then 'gap' is used to    * compute the effective endpoint closest to the 'end' param which    * results in the range between 'start' and 'end' being evenly    * divisible by 'gap'.    * The default is false.    * Can be overriden on a per field basis.    */
DECL|field|FACET_DATE_HARD_END
specifier|public
specifier|static
specifier|final
name|String
name|FACET_DATE_HARD_END
init|=
literal|"facet.date.hardend"
decl_stmt|;
comment|/**    * String indicating what "other" ranges should be computed for a    * date facet range (multi-value).    * Can be overriden on a per field basis.    * @see FacetDateOther    */
DECL|field|FACET_DATE_OTHER
specifier|public
specifier|static
specifier|final
name|String
name|FACET_DATE_OTHER
init|=
literal|"facet.date.other"
decl_stmt|;
comment|/**    * An enumeration of the legal values for FACET_DATE_OTHER...    *<ul>    *<li>before = the count of matches before the start date</li>    *<li>after = the count of matches after the end date</li>    *<li>between = the count of all matches between start and end</li>    *<li>all = all of the above (default value)</li>    *<li>none = no additional info requested</li>    *</ul>    * @see #FACET_DATE_OTHER    */
DECL|enum|FacetDateOther
specifier|public
enum|enum
name|FacetDateOther
block|{
DECL|enum constant|BEFORE
DECL|enum constant|AFTER
DECL|enum constant|BETWEEN
DECL|enum constant|ALL
DECL|enum constant|NONE
name|BEFORE
block|,
name|AFTER
block|,
name|BETWEEN
block|,
name|ALL
block|,
name|NONE
block|;
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|super
operator|.
name|toString
argument_list|()
operator|.
name|toLowerCase
argument_list|()
return|;
block|}
DECL|method|get
specifier|public
specifier|static
name|FacetDateOther
name|get
parameter_list|(
name|String
name|label
parameter_list|)
block|{
try|try
block|{
return|return
name|valueOf
argument_list|(
name|label
operator|.
name|toUpperCase
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
name|label
operator|+
literal|" is not a valid type of 'other' date facet information"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
block|}
end_interface

end_unit

