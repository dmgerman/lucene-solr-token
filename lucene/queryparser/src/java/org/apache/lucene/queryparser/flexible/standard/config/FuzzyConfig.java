begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.queryparser.flexible.standard.config
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryparser
operator|.
name|flexible
operator|.
name|standard
operator|.
name|config
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
name|search
operator|.
name|FuzzyQuery
import|;
end_import

begin_comment
comment|/**  * Configuration parameters for {@link FuzzyQuery}s  */
end_comment

begin_class
DECL|class|FuzzyConfig
specifier|public
class|class
name|FuzzyConfig
block|{
DECL|field|prefixLength
specifier|private
name|int
name|prefixLength
init|=
name|FuzzyQuery
operator|.
name|defaultPrefixLength
decl_stmt|;
DECL|field|minSimilarity
specifier|private
name|float
name|minSimilarity
init|=
name|FuzzyQuery
operator|.
name|defaultMinSimilarity
decl_stmt|;
DECL|method|FuzzyConfig
specifier|public
name|FuzzyConfig
parameter_list|()
block|{}
DECL|method|getPrefixLength
specifier|public
name|int
name|getPrefixLength
parameter_list|()
block|{
return|return
name|prefixLength
return|;
block|}
DECL|method|setPrefixLength
specifier|public
name|void
name|setPrefixLength
parameter_list|(
name|int
name|prefixLength
parameter_list|)
block|{
name|this
operator|.
name|prefixLength
operator|=
name|prefixLength
expr_stmt|;
block|}
DECL|method|getMinSimilarity
specifier|public
name|float
name|getMinSimilarity
parameter_list|()
block|{
return|return
name|minSimilarity
return|;
block|}
DECL|method|setMinSimilarity
specifier|public
name|void
name|setMinSimilarity
parameter_list|(
name|float
name|minSimilarity
parameter_list|)
block|{
name|this
operator|.
name|minSimilarity
operator|=
name|minSimilarity
expr_stmt|;
block|}
block|}
end_class

end_unit

