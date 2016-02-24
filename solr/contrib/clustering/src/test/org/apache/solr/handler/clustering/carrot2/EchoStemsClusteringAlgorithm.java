begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.handler.clustering.carrot2
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|clustering
operator|.
name|carrot2
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|org
operator|.
name|carrot2
operator|.
name|core
operator|.
name|Cluster
import|;
end_import

begin_import
import|import
name|org
operator|.
name|carrot2
operator|.
name|core
operator|.
name|Document
import|;
end_import

begin_import
import|import
name|org
operator|.
name|carrot2
operator|.
name|core
operator|.
name|IClusteringAlgorithm
import|;
end_import

begin_import
import|import
name|org
operator|.
name|carrot2
operator|.
name|core
operator|.
name|LanguageCode
import|;
end_import

begin_import
import|import
name|org
operator|.
name|carrot2
operator|.
name|core
operator|.
name|ProcessingComponentBase
import|;
end_import

begin_import
import|import
name|org
operator|.
name|carrot2
operator|.
name|core
operator|.
name|ProcessingException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|carrot2
operator|.
name|core
operator|.
name|attribute
operator|.
name|AttributeNames
import|;
end_import

begin_import
import|import
name|org
operator|.
name|carrot2
operator|.
name|core
operator|.
name|attribute
operator|.
name|Processing
import|;
end_import

begin_import
import|import
name|org
operator|.
name|carrot2
operator|.
name|text
operator|.
name|preprocessing
operator|.
name|PreprocessingContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|carrot2
operator|.
name|text
operator|.
name|preprocessing
operator|.
name|PreprocessingContext
operator|.
name|AllStems
import|;
end_import

begin_import
import|import
name|org
operator|.
name|carrot2
operator|.
name|text
operator|.
name|preprocessing
operator|.
name|PreprocessingContext
operator|.
name|AllTokens
import|;
end_import

begin_import
import|import
name|org
operator|.
name|carrot2
operator|.
name|text
operator|.
name|preprocessing
operator|.
name|PreprocessingContext
operator|.
name|AllWords
import|;
end_import

begin_import
import|import
name|org
operator|.
name|carrot2
operator|.
name|text
operator|.
name|preprocessing
operator|.
name|pipeline
operator|.
name|BasicPreprocessingPipeline
import|;
end_import

begin_import
import|import
name|org
operator|.
name|carrot2
operator|.
name|util
operator|.
name|attribute
operator|.
name|Attribute
import|;
end_import

begin_import
import|import
name|org
operator|.
name|carrot2
operator|.
name|util
operator|.
name|attribute
operator|.
name|Bindable
import|;
end_import

begin_import
import|import
name|org
operator|.
name|carrot2
operator|.
name|util
operator|.
name|attribute
operator|.
name|Input
import|;
end_import

begin_import
import|import
name|org
operator|.
name|carrot2
operator|.
name|util
operator|.
name|attribute
operator|.
name|Output
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
import|;
end_import

begin_comment
comment|/**  * A mock Carrot2 clustering algorithm that outputs stem of each token of each  * document as a separate cluster. Useful only in tests.  */
end_comment

begin_class
annotation|@
name|Bindable
argument_list|(
name|prefix
operator|=
literal|"EchoTokensClusteringAlgorithm"
argument_list|)
DECL|class|EchoStemsClusteringAlgorithm
specifier|public
class|class
name|EchoStemsClusteringAlgorithm
extends|extends
name|ProcessingComponentBase
implements|implements
name|IClusteringAlgorithm
block|{
annotation|@
name|Input
annotation|@
name|Processing
annotation|@
name|Attribute
argument_list|(
name|key
operator|=
name|AttributeNames
operator|.
name|DOCUMENTS
argument_list|)
DECL|field|documents
specifier|public
name|List
argument_list|<
name|Document
argument_list|>
name|documents
decl_stmt|;
annotation|@
name|Output
annotation|@
name|Processing
annotation|@
name|Attribute
argument_list|(
name|key
operator|=
name|AttributeNames
operator|.
name|CLUSTERS
argument_list|)
DECL|field|clusters
specifier|public
name|List
argument_list|<
name|Cluster
argument_list|>
name|clusters
decl_stmt|;
DECL|field|preprocessing
specifier|public
name|BasicPreprocessingPipeline
name|preprocessing
init|=
operator|new
name|BasicPreprocessingPipeline
argument_list|()
decl_stmt|;
annotation|@
name|Override
DECL|method|process
specifier|public
name|void
name|process
parameter_list|()
throws|throws
name|ProcessingException
block|{
specifier|final
name|PreprocessingContext
name|preprocessingContext
init|=
name|preprocessing
operator|.
name|preprocess
argument_list|(
name|documents
argument_list|,
literal|""
argument_list|,
name|LanguageCode
operator|.
name|ENGLISH
argument_list|)
decl_stmt|;
specifier|final
name|AllTokens
name|allTokens
init|=
name|preprocessingContext
operator|.
name|allTokens
decl_stmt|;
specifier|final
name|AllWords
name|allWords
init|=
name|preprocessingContext
operator|.
name|allWords
decl_stmt|;
specifier|final
name|AllStems
name|allStems
init|=
name|preprocessingContext
operator|.
name|allStems
decl_stmt|;
name|clusters
operator|=
name|Lists
operator|.
name|newArrayListWithCapacity
argument_list|(
name|allTokens
operator|.
name|image
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|allTokens
operator|.
name|image
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|allTokens
operator|.
name|wordIndex
index|[
name|i
index|]
operator|>=
literal|0
condition|)
block|{
name|clusters
operator|.
name|add
argument_list|(
operator|new
name|Cluster
argument_list|(
operator|new
name|String
argument_list|(
name|allStems
operator|.
name|image
index|[
name|allWords
operator|.
name|stemIndex
index|[
name|allTokens
operator|.
name|wordIndex
index|[
name|i
index|]
index|]
index|]
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

