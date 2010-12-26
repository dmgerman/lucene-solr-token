begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.search.function
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|function
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
name|*
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
name|Searcher
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
name|Similarity
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
name|util
operator|.
name|ByteUtils
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/** @lucene.internal */
end_comment

begin_class
DECL|class|IDFValueSource
specifier|public
class|class
name|IDFValueSource
extends|extends
name|DocFreqValueSource
block|{
DECL|method|IDFValueSource
specifier|public
name|IDFValueSource
parameter_list|(
name|String
name|field
parameter_list|,
name|String
name|val
parameter_list|,
name|String
name|indexedField
parameter_list|,
name|BytesRef
name|indexedBytes
parameter_list|)
block|{
name|super
argument_list|(
name|field
argument_list|,
name|val
argument_list|,
name|indexedField
argument_list|,
name|indexedBytes
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|name
specifier|public
name|String
name|name
parameter_list|()
block|{
return|return
literal|"idf"
return|;
block|}
annotation|@
name|Override
DECL|method|getValues
specifier|public
name|DocValues
name|getValues
parameter_list|(
name|Map
name|context
parameter_list|,
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
name|Searcher
name|searcher
init|=
operator|(
name|Searcher
operator|)
name|context
operator|.
name|get
argument_list|(
literal|"searcher"
argument_list|)
decl_stmt|;
name|Similarity
name|sim
init|=
name|searcher
operator|.
name|getSimilarity
argument_list|()
decl_stmt|;
comment|// todo: we need docFreq that takes a BytesRef
name|String
name|strVal
init|=
name|ByteUtils
operator|.
name|UTF8toUTF16
argument_list|(
name|indexedBytes
argument_list|)
decl_stmt|;
name|int
name|docfreq
init|=
name|searcher
operator|.
name|docFreq
argument_list|(
operator|new
name|Term
argument_list|(
name|indexedField
argument_list|,
name|strVal
argument_list|)
argument_list|)
decl_stmt|;
name|float
name|idf
init|=
name|sim
operator|.
name|idf
argument_list|(
name|docfreq
argument_list|,
name|searcher
operator|.
name|maxDoc
argument_list|()
argument_list|)
decl_stmt|;
return|return
operator|new
name|ConstDoubleDocValues
argument_list|(
name|idf
argument_list|,
name|this
argument_list|)
return|;
block|}
block|}
end_class

end_unit

