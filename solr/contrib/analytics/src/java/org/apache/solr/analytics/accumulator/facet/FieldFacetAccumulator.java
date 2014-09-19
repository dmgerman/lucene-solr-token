begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.analytics.accumulator.facet
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|analytics
operator|.
name|accumulator
operator|.
name|facet
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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
name|index
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
name|index
operator|.
name|NumericDocValues
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
name|index
operator|.
name|SortedDocValues
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
name|index
operator|.
name|SortedSetDocValues
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
name|BytesRef
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|analytics
operator|.
name|accumulator
operator|.
name|FacetingAccumulator
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|analytics
operator|.
name|accumulator
operator|.
name|ValueAccumulator
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|analytics
operator|.
name|util
operator|.
name|AnalyticsParsers
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|analytics
operator|.
name|util
operator|.
name|AnalyticsParsers
operator|.
name|NumericParser
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|analytics
operator|.
name|util
operator|.
name|AnalyticsParsers
operator|.
name|Parser
import|;
end_import

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
operator|.
name|ErrorCode
import|;
end_import

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
name|util
operator|.
name|NamedList
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|schema
operator|.
name|SchemaField
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|schema
operator|.
name|TrieDateField
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|SolrIndexSearcher
import|;
end_import

begin_comment
comment|/**  * An Accumulator that manages the faceting for fieldFacets.  * Collects the field facet values.  */
end_comment

begin_class
DECL|class|FieldFacetAccumulator
specifier|public
class|class
name|FieldFacetAccumulator
extends|extends
name|ValueAccumulator
block|{
DECL|field|parser
specifier|protected
specifier|final
name|Parser
name|parser
decl_stmt|;
DECL|field|parent
specifier|protected
specifier|final
name|FacetValueAccumulator
name|parent
decl_stmt|;
DECL|field|name
specifier|protected
specifier|final
name|String
name|name
decl_stmt|;
DECL|field|searcher
specifier|protected
specifier|final
name|SolrIndexSearcher
name|searcher
decl_stmt|;
DECL|field|schemaField
specifier|protected
specifier|final
name|SchemaField
name|schemaField
decl_stmt|;
DECL|field|multiValued
specifier|protected
specifier|final
name|boolean
name|multiValued
decl_stmt|;
DECL|field|numField
specifier|protected
specifier|final
name|boolean
name|numField
decl_stmt|;
DECL|field|dateField
specifier|protected
specifier|final
name|boolean
name|dateField
decl_stmt|;
DECL|field|setValues
specifier|protected
name|SortedSetDocValues
name|setValues
decl_stmt|;
DECL|field|sortValues
specifier|protected
name|SortedDocValues
name|sortValues
decl_stmt|;
DECL|field|numValues
specifier|protected
name|NumericDocValues
name|numValues
decl_stmt|;
DECL|field|numValuesBits
specifier|protected
name|Bits
name|numValuesBits
decl_stmt|;
DECL|method|FieldFacetAccumulator
specifier|public
name|FieldFacetAccumulator
parameter_list|(
name|SolrIndexSearcher
name|searcher
parameter_list|,
name|FacetValueAccumulator
name|parent
parameter_list|,
name|SchemaField
name|schemaField
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|schemaField
operator|.
name|hasDocValues
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"Field '"
operator|+
name|schemaField
operator|.
name|getName
argument_list|()
operator|+
literal|"' does not have docValues"
argument_list|)
throw|;
block|}
name|this
operator|.
name|searcher
operator|=
name|searcher
expr_stmt|;
name|this
operator|.
name|schemaField
operator|=
name|schemaField
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|schemaField
operator|.
name|getName
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|schemaField
operator|.
name|hasDocValues
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|name
operator|+
literal|" does not have docValues and therefore cannot be faceted over."
argument_list|)
throw|;
block|}
name|this
operator|.
name|multiValued
operator|=
name|schemaField
operator|.
name|multiValued
argument_list|()
expr_stmt|;
name|this
operator|.
name|numField
operator|=
name|schemaField
operator|.
name|getType
argument_list|()
operator|.
name|getNumericType
argument_list|()
operator|!=
literal|null
expr_stmt|;
name|this
operator|.
name|dateField
operator|=
name|schemaField
operator|.
name|getType
argument_list|()
operator|.
name|getClass
argument_list|()
operator|.
name|equals
argument_list|(
name|TrieDateField
operator|.
name|class
argument_list|)
expr_stmt|;
name|this
operator|.
name|parent
operator|=
name|parent
expr_stmt|;
name|this
operator|.
name|parser
operator|=
name|AnalyticsParsers
operator|.
name|getParser
argument_list|(
name|schemaField
operator|.
name|getType
argument_list|()
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|create
specifier|public
specifier|static
name|FieldFacetAccumulator
name|create
parameter_list|(
name|SolrIndexSearcher
name|searcher
parameter_list|,
name|FacetValueAccumulator
name|parent
parameter_list|,
name|SchemaField
name|facetField
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|FieldFacetAccumulator
argument_list|(
name|searcher
argument_list|,
name|parent
argument_list|,
name|facetField
argument_list|)
return|;
block|}
comment|/**    * Move to the next set of documents to add to the field facet.    */
annotation|@
name|Override
DECL|method|doSetNextReader
specifier|protected
name|void
name|doSetNextReader
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|multiValued
condition|)
block|{
name|setValues
operator|=
name|context
operator|.
name|reader
argument_list|()
operator|.
name|getSortedSetDocValues
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|numField
condition|)
block|{
name|numValues
operator|=
name|context
operator|.
name|reader
argument_list|()
operator|.
name|getNumericDocValues
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|numValuesBits
operator|=
name|context
operator|.
name|reader
argument_list|()
operator|.
name|getDocsWithField
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|sortValues
operator|=
name|context
operator|.
name|reader
argument_list|()
operator|.
name|getSortedDocValues
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Tell the FacetingAccumulator to collect the doc with the     * given fieldFacet and value(s).    */
annotation|@
name|Override
DECL|method|collect
specifier|public
name|void
name|collect
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|multiValued
condition|)
block|{
name|boolean
name|exists
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|setValues
operator|!=
literal|null
condition|)
block|{
name|setValues
operator|.
name|setDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|int
name|term
decl_stmt|;
while|while
condition|(
operator|(
name|term
operator|=
operator|(
name|int
operator|)
name|setValues
operator|.
name|nextOrd
argument_list|()
operator|)
operator|!=
name|SortedSetDocValues
operator|.
name|NO_MORE_ORDS
condition|)
block|{
name|exists
operator|=
literal|true
expr_stmt|;
specifier|final
name|BytesRef
name|value
init|=
name|setValues
operator|.
name|lookupOrd
argument_list|(
name|term
argument_list|)
decl_stmt|;
name|parent
operator|.
name|collectField
argument_list|(
name|doc
argument_list|,
name|name
argument_list|,
name|parser
operator|.
name|parse
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|exists
condition|)
block|{
name|parent
operator|.
name|collectField
argument_list|(
name|doc
argument_list|,
name|name
argument_list|,
name|FacetingAccumulator
operator|.
name|MISSING_VALUE
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|numField
condition|)
block|{
if|if
condition|(
name|numValues
operator|!=
literal|null
condition|)
block|{
name|long
name|v
init|=
name|numValues
operator|.
name|get
argument_list|(
name|doc
argument_list|)
decl_stmt|;
if|if
condition|(
name|v
operator|!=
literal|0
operator|||
name|numValuesBits
operator|.
name|get
argument_list|(
name|doc
argument_list|)
condition|)
block|{
name|parent
operator|.
name|collectField
argument_list|(
name|doc
argument_list|,
name|name
argument_list|,
operator|(
operator|(
name|NumericParser
operator|)
name|parser
operator|)
operator|.
name|parseNum
argument_list|(
name|v
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|parent
operator|.
name|collectField
argument_list|(
name|doc
argument_list|,
name|name
argument_list|,
name|FacetingAccumulator
operator|.
name|MISSING_VALUE
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|parent
operator|.
name|collectField
argument_list|(
name|doc
argument_list|,
name|name
argument_list|,
name|FacetingAccumulator
operator|.
name|MISSING_VALUE
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|sortValues
operator|!=
literal|null
condition|)
block|{
specifier|final
name|int
name|ord
init|=
name|sortValues
operator|.
name|getOrd
argument_list|(
name|doc
argument_list|)
decl_stmt|;
if|if
condition|(
name|ord
operator|<
literal|0
condition|)
block|{
name|parent
operator|.
name|collectField
argument_list|(
name|doc
argument_list|,
name|name
argument_list|,
name|FacetingAccumulator
operator|.
name|MISSING_VALUE
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|parent
operator|.
name|collectField
argument_list|(
name|doc
argument_list|,
name|name
argument_list|,
name|parser
operator|.
name|parse
argument_list|(
name|sortValues
operator|.
name|lookupOrd
argument_list|(
name|ord
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|parent
operator|.
name|collectField
argument_list|(
name|doc
argument_list|,
name|name
argument_list|,
name|FacetingAccumulator
operator|.
name|MISSING_VALUE
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|compute
specifier|public
name|void
name|compute
parameter_list|()
block|{}
annotation|@
name|Override
DECL|method|export
specifier|public
name|NamedList
argument_list|<
name|?
argument_list|>
name|export
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit
