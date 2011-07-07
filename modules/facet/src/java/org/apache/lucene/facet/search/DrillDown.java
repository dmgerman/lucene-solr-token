begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.facet.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|search
package|;
end_package

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
name|Term
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
name|BooleanQuery
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
name|Query
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
name|TermQuery
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
name|BooleanClause
operator|.
name|Occur
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
name|facet
operator|.
name|index
operator|.
name|params
operator|.
name|CategoryListParams
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
name|facet
operator|.
name|index
operator|.
name|params
operator|.
name|FacetIndexingParams
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
name|facet
operator|.
name|search
operator|.
name|params
operator|.
name|FacetSearchParams
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
name|facet
operator|.
name|taxonomy
operator|.
name|CategoryPath
import|;
end_import

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * Creation of drill down term or query.  *   * @lucene.experimental  */
end_comment

begin_class
DECL|class|DrillDown
specifier|public
specifier|final
class|class
name|DrillDown
block|{
comment|/**    * @see #term(FacetIndexingParams, CategoryPath)    */
DECL|method|term
specifier|public
specifier|static
specifier|final
name|Term
name|term
parameter_list|(
name|FacetSearchParams
name|sParams
parameter_list|,
name|CategoryPath
name|path
parameter_list|)
block|{
return|return
name|term
argument_list|(
name|sParams
operator|.
name|getFacetIndexingParams
argument_list|()
argument_list|,
name|path
argument_list|)
return|;
block|}
comment|/**    * Return a term for drilling down into a category.    */
DECL|method|term
specifier|public
specifier|static
specifier|final
name|Term
name|term
parameter_list|(
name|FacetIndexingParams
name|iParams
parameter_list|,
name|CategoryPath
name|path
parameter_list|)
block|{
name|CategoryListParams
name|clp
init|=
name|iParams
operator|.
name|getCategoryListParams
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|char
index|[]
name|buffer
init|=
operator|new
name|char
index|[
name|path
operator|.
name|charsNeededForFullPath
argument_list|()
index|]
decl_stmt|;
name|iParams
operator|.
name|drillDownTermText
argument_list|(
name|path
argument_list|,
name|buffer
argument_list|)
expr_stmt|;
return|return
operator|new
name|Term
argument_list|(
name|clp
operator|.
name|getTerm
argument_list|()
operator|.
name|field
argument_list|()
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|buffer
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Return a query for drilling down into all given categories (AND).    * @see #term(FacetSearchParams, CategoryPath)    * @see #query(FacetSearchParams, Query, CategoryPath...)    */
DECL|method|query
specifier|public
specifier|static
specifier|final
name|Query
name|query
parameter_list|(
name|FacetIndexingParams
name|iParams
parameter_list|,
name|CategoryPath
modifier|...
name|paths
parameter_list|)
block|{
if|if
condition|(
name|paths
operator|==
literal|null
operator|||
name|paths
operator|.
name|length
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Empty category path not allowed for drill down query!"
argument_list|)
throw|;
block|}
if|if
condition|(
name|paths
operator|.
name|length
operator|==
literal|1
condition|)
block|{
return|return
operator|new
name|TermQuery
argument_list|(
name|term
argument_list|(
name|iParams
argument_list|,
name|paths
index|[
literal|0
index|]
argument_list|)
argument_list|)
return|;
block|}
name|BooleanQuery
name|res
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
for|for
control|(
name|CategoryPath
name|cp
range|:
name|paths
control|)
block|{
name|res
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
name|term
argument_list|(
name|iParams
argument_list|,
name|cp
argument_list|)
argument_list|)
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
block|}
return|return
name|res
return|;
block|}
comment|/**    * Return a query for drilling down into all given categories (AND).    * @see #term(FacetSearchParams, CategoryPath)    * @see #query(FacetSearchParams, Query, CategoryPath...)    */
DECL|method|query
specifier|public
specifier|static
specifier|final
name|Query
name|query
parameter_list|(
name|FacetSearchParams
name|sParams
parameter_list|,
name|CategoryPath
modifier|...
name|paths
parameter_list|)
block|{
return|return
name|query
argument_list|(
name|sParams
operator|.
name|getFacetIndexingParams
argument_list|()
argument_list|,
name|paths
argument_list|)
return|;
block|}
comment|/**    * Turn a base query into a drilling-down query for all given category paths (AND).    * @see #query(FacetIndexingParams, CategoryPath...)    */
DECL|method|query
specifier|public
specifier|static
specifier|final
name|Query
name|query
parameter_list|(
name|FacetIndexingParams
name|iParams
parameter_list|,
name|Query
name|baseQuery
parameter_list|,
name|CategoryPath
modifier|...
name|paths
parameter_list|)
block|{
name|BooleanQuery
name|res
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|res
operator|.
name|add
argument_list|(
name|baseQuery
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|res
operator|.
name|add
argument_list|(
name|query
argument_list|(
name|iParams
argument_list|,
name|paths
argument_list|)
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
return|return
name|res
return|;
block|}
comment|/**    * Turn a base query into a drilling-down query for all given category paths (AND).    * @see #query(FacetSearchParams, CategoryPath...)    */
DECL|method|query
specifier|public
specifier|static
specifier|final
name|Query
name|query
parameter_list|(
name|FacetSearchParams
name|sParams
parameter_list|,
name|Query
name|baseQuery
parameter_list|,
name|CategoryPath
modifier|...
name|paths
parameter_list|)
block|{
return|return
name|query
argument_list|(
name|sParams
operator|.
name|getFacetIndexingParams
argument_list|()
argument_list|,
name|baseQuery
argument_list|,
name|paths
argument_list|)
return|;
block|}
comment|/**    * Turn a base query into a drilling-down query using the default {@link FacetSearchParams}      * @see #query(FacetSearchParams, Query, CategoryPath...)    */
DECL|method|query
specifier|public
specifier|static
specifier|final
name|Query
name|query
parameter_list|(
name|Query
name|baseQuery
parameter_list|,
name|CategoryPath
modifier|...
name|paths
parameter_list|)
block|{
return|return
name|query
argument_list|(
operator|new
name|FacetSearchParams
argument_list|()
argument_list|,
name|baseQuery
argument_list|,
name|paths
argument_list|)
return|;
block|}
block|}
end_class

end_unit

