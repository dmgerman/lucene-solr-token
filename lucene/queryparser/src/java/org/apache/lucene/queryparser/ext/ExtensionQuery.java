begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.queryparser.ext
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryparser
operator|.
name|ext
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
name|queryparser
operator|.
name|classic
operator|.
name|QueryParser
import|;
end_import

begin_comment
comment|/**  * {@link ExtensionQuery} holds all query components extracted from the original  * query string like the query field and the extension query string.  *   * @see Extensions  * @see ExtendableQueryParser  * @see ParserExtension  */
end_comment

begin_class
DECL|class|ExtensionQuery
specifier|public
class|class
name|ExtensionQuery
block|{
DECL|field|field
specifier|private
specifier|final
name|String
name|field
decl_stmt|;
DECL|field|rawQueryString
specifier|private
specifier|final
name|String
name|rawQueryString
decl_stmt|;
DECL|field|topLevelParser
specifier|private
specifier|final
name|QueryParser
name|topLevelParser
decl_stmt|;
comment|/**    * Creates a new {@link ExtensionQuery}    *     * @param field    *          the query field    * @param rawQueryString    *          the raw extension query string    */
DECL|method|ExtensionQuery
specifier|public
name|ExtensionQuery
parameter_list|(
name|QueryParser
name|topLevelParser
parameter_list|,
name|String
name|field
parameter_list|,
name|String
name|rawQueryString
parameter_list|)
block|{
name|this
operator|.
name|field
operator|=
name|field
expr_stmt|;
name|this
operator|.
name|rawQueryString
operator|=
name|rawQueryString
expr_stmt|;
name|this
operator|.
name|topLevelParser
operator|=
name|topLevelParser
expr_stmt|;
block|}
comment|/**    * Returns the query field    *     * @return the query field    */
DECL|method|getField
specifier|public
name|String
name|getField
parameter_list|()
block|{
return|return
name|field
return|;
block|}
comment|/**    * Returns the raw extension query string    *     * @return the raw extension query string    */
DECL|method|getRawQueryString
specifier|public
name|String
name|getRawQueryString
parameter_list|()
block|{
return|return
name|rawQueryString
return|;
block|}
comment|/**    * Returns the top level parser which created this {@link ExtensionQuery}     * @return the top level parser which created this {@link ExtensionQuery}    */
DECL|method|getTopLevelParser
specifier|public
name|QueryParser
name|getTopLevelParser
parameter_list|()
block|{
return|return
name|topLevelParser
return|;
block|}
block|}
end_class

end_unit

