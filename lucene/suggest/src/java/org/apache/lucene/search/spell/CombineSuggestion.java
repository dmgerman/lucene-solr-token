begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search.spell
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|spell
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_class
DECL|class|CombineSuggestion
specifier|public
class|class
name|CombineSuggestion
block|{
comment|/**    *<p>The indexes from the passed-in array of terms used to make this word combination</p>    */
DECL|field|originalTermIndexes
specifier|public
specifier|final
name|int
index|[]
name|originalTermIndexes
decl_stmt|;
comment|/**    *<p>The word combination suggestion</p>    */
DECL|field|suggestion
specifier|public
specifier|final
name|SuggestWord
name|suggestion
decl_stmt|;
DECL|method|CombineSuggestion
specifier|public
name|CombineSuggestion
parameter_list|(
name|SuggestWord
name|suggestion
parameter_list|,
name|int
index|[]
name|originalTermIndexes
parameter_list|)
block|{
name|this
operator|.
name|suggestion
operator|=
name|suggestion
expr_stmt|;
name|this
operator|.
name|originalTermIndexes
operator|=
name|originalTermIndexes
expr_stmt|;
block|}
block|}
end_class

end_unit

