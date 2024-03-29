begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.search.facet
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
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
name|TermsEnum
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
name|lucene
operator|.
name|util
operator|.
name|UnicodeUtil
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

begin_comment
comment|/** {@link UnInvertedField} implementation of field faceting.  * It's a top-level term cache. */
end_comment

begin_class
DECL|class|FacetFieldProcessorByArrayUIF
class|class
name|FacetFieldProcessorByArrayUIF
extends|extends
name|FacetFieldProcessorByArray
block|{
DECL|field|uif
name|UnInvertedField
name|uif
decl_stmt|;
DECL|field|te
name|TermsEnum
name|te
decl_stmt|;
DECL|method|FacetFieldProcessorByArrayUIF
name|FacetFieldProcessorByArrayUIF
parameter_list|(
name|FacetContext
name|fcontext
parameter_list|,
name|FacetField
name|freq
parameter_list|,
name|SchemaField
name|sf
parameter_list|)
block|{
name|super
argument_list|(
name|fcontext
argument_list|,
name|freq
argument_list|,
name|sf
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|findStartAndEndOrds
specifier|protected
name|void
name|findStartAndEndOrds
parameter_list|()
throws|throws
name|IOException
block|{
name|uif
operator|=
name|UnInvertedField
operator|.
name|getUnInvertedField
argument_list|(
name|freq
operator|.
name|field
argument_list|,
name|fcontext
operator|.
name|searcher
argument_list|)
expr_stmt|;
name|te
operator|=
name|uif
operator|.
name|getOrdTermsEnum
argument_list|(
name|fcontext
operator|.
name|searcher
operator|.
name|getSlowAtomicReader
argument_list|()
argument_list|)
expr_stmt|;
comment|// "te" can be null
name|startTermIndex
operator|=
literal|0
expr_stmt|;
name|endTermIndex
operator|=
name|uif
operator|.
name|numTerms
argument_list|()
expr_stmt|;
comment|// one past the end
if|if
condition|(
name|prefixRef
operator|!=
literal|null
operator|&&
name|te
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|te
operator|.
name|seekCeil
argument_list|(
name|prefixRef
operator|.
name|get
argument_list|()
argument_list|)
operator|==
name|TermsEnum
operator|.
name|SeekStatus
operator|.
name|END
condition|)
block|{
name|startTermIndex
operator|=
name|uif
operator|.
name|numTerms
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|startTermIndex
operator|=
operator|(
name|int
operator|)
name|te
operator|.
name|ord
argument_list|()
expr_stmt|;
block|}
name|prefixRef
operator|.
name|append
argument_list|(
name|UnicodeUtil
operator|.
name|BIG_TERM
argument_list|)
expr_stmt|;
if|if
condition|(
name|te
operator|.
name|seekCeil
argument_list|(
name|prefixRef
operator|.
name|get
argument_list|()
argument_list|)
operator|==
name|TermsEnum
operator|.
name|SeekStatus
operator|.
name|END
condition|)
block|{
name|endTermIndex
operator|=
name|uif
operator|.
name|numTerms
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|endTermIndex
operator|=
operator|(
name|int
operator|)
name|te
operator|.
name|ord
argument_list|()
expr_stmt|;
block|}
block|}
name|nTerms
operator|=
name|endTermIndex
operator|-
name|startTermIndex
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|collectDocs
specifier|protected
name|void
name|collectDocs
parameter_list|()
throws|throws
name|IOException
block|{
name|uif
operator|.
name|collectDocs
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|lookupOrd
specifier|protected
name|BytesRef
name|lookupOrd
parameter_list|(
name|int
name|ord
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|uif
operator|.
name|getTermValue
argument_list|(
name|te
argument_list|,
name|ord
argument_list|)
return|;
block|}
block|}
end_class

end_unit

