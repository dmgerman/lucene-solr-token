begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.benchmark.byTask.feeds.demohtml
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|benchmark
operator|.
name|byTask
operator|.
name|feeds
operator|.
name|demohtml
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_comment
comment|/**  * Utility class storing set of commonly-used html tags.  */
end_comment

begin_class
DECL|class|Tags
specifier|public
specifier|final
class|class
name|Tags
block|{
comment|/**    * contains all tags for which whitespaces have to be inserted for proper tokenization    */
DECL|field|WS_ELEMS
specifier|public
specifier|static
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|WS_ELEMS
decl_stmt|;
static|static
block|{
name|WS_ELEMS
operator|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
name|WS_ELEMS
operator|.
name|add
argument_list|(
literal|"<hr"
argument_list|)
expr_stmt|;
name|WS_ELEMS
operator|.
name|add
argument_list|(
literal|"<hr/"
argument_list|)
expr_stmt|;
comment|// note that "<hr />" does not need to be listed explicitly
name|WS_ELEMS
operator|.
name|add
argument_list|(
literal|"<br"
argument_list|)
expr_stmt|;
name|WS_ELEMS
operator|.
name|add
argument_list|(
literal|"<br/"
argument_list|)
expr_stmt|;
name|WS_ELEMS
operator|.
name|add
argument_list|(
literal|"<p"
argument_list|)
expr_stmt|;
name|WS_ELEMS
operator|.
name|add
argument_list|(
literal|"</p"
argument_list|)
expr_stmt|;
name|WS_ELEMS
operator|.
name|add
argument_list|(
literal|"<div"
argument_list|)
expr_stmt|;
name|WS_ELEMS
operator|.
name|add
argument_list|(
literal|"</div"
argument_list|)
expr_stmt|;
name|WS_ELEMS
operator|.
name|add
argument_list|(
literal|"<td"
argument_list|)
expr_stmt|;
name|WS_ELEMS
operator|.
name|add
argument_list|(
literal|"</td"
argument_list|)
expr_stmt|;
name|WS_ELEMS
operator|.
name|add
argument_list|(
literal|"<li"
argument_list|)
expr_stmt|;
name|WS_ELEMS
operator|.
name|add
argument_list|(
literal|"</li"
argument_list|)
expr_stmt|;
name|WS_ELEMS
operator|.
name|add
argument_list|(
literal|"<q"
argument_list|)
expr_stmt|;
name|WS_ELEMS
operator|.
name|add
argument_list|(
literal|"</q"
argument_list|)
expr_stmt|;
name|WS_ELEMS
operator|.
name|add
argument_list|(
literal|"<blockquote"
argument_list|)
expr_stmt|;
name|WS_ELEMS
operator|.
name|add
argument_list|(
literal|"</blockquote"
argument_list|)
expr_stmt|;
name|WS_ELEMS
operator|.
name|add
argument_list|(
literal|"<dt"
argument_list|)
expr_stmt|;
name|WS_ELEMS
operator|.
name|add
argument_list|(
literal|"</dt"
argument_list|)
expr_stmt|;
name|WS_ELEMS
operator|.
name|add
argument_list|(
literal|"<h1"
argument_list|)
expr_stmt|;
name|WS_ELEMS
operator|.
name|add
argument_list|(
literal|"</h1"
argument_list|)
expr_stmt|;
name|WS_ELEMS
operator|.
name|add
argument_list|(
literal|"<h2"
argument_list|)
expr_stmt|;
name|WS_ELEMS
operator|.
name|add
argument_list|(
literal|"</h2"
argument_list|)
expr_stmt|;
name|WS_ELEMS
operator|.
name|add
argument_list|(
literal|"<h3"
argument_list|)
expr_stmt|;
name|WS_ELEMS
operator|.
name|add
argument_list|(
literal|"</h3"
argument_list|)
expr_stmt|;
name|WS_ELEMS
operator|.
name|add
argument_list|(
literal|"<h4"
argument_list|)
expr_stmt|;
name|WS_ELEMS
operator|.
name|add
argument_list|(
literal|"</h4"
argument_list|)
expr_stmt|;
name|WS_ELEMS
operator|.
name|add
argument_list|(
literal|"<h5"
argument_list|)
expr_stmt|;
name|WS_ELEMS
operator|.
name|add
argument_list|(
literal|"</h5"
argument_list|)
expr_stmt|;
name|WS_ELEMS
operator|.
name|add
argument_list|(
literal|"<h6"
argument_list|)
expr_stmt|;
name|WS_ELEMS
operator|.
name|add
argument_list|(
literal|"</h6"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

