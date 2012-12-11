begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.path
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|path
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Reader
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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|Tokenizer
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
name|analysis
operator|.
name|path
operator|.
name|PathHierarchyTokenizer
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
name|analysis
operator|.
name|path
operator|.
name|ReversePathHierarchyTokenizer
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
name|analysis
operator|.
name|util
operator|.
name|TokenizerFactory
import|;
end_import

begin_comment
comment|/**  * Factory for {@link PathHierarchyTokenizer}.   *<p>  * This factory is typically configured for use only in the<code>index</code>   * Analyzer (or only in the<code>query</code> Analyzer, but never both).  *</p>  *<p>  * For example, in the configuration below a query for   *<code>Books/NonFic</code> will match documents indexed with values like   *<code>Books/NonFic</code>,<code>Books/NonFic/Law</code>,   *<code>Books/NonFic/Science/Physics</code>, etc. But it will not match   * documents indexed with values like<code>Books</code>, or   *<code>Books/Fic</code>...  *</p>  *  *<pre class="prettyprint">  *&lt;fieldType name="descendent_path" class="solr.TextField"&gt;  *&lt;analyzer type="index"&gt;  *&lt;tokenizer class="solr.PathHierarchyTokenizerFactory" delimiter="/" /&gt;  *&lt;/analyzer&gt;  *&lt;analyzer type="query"&gt;  *&lt;tokenizer class="solr.KeywordTokenizerFactory" /&gt;  *&lt;/analyzer&gt;  *&lt;/fieldType&gt;  *</pre>  *<p>  * In this example however we see the oposite configuration, so that a query   * for<code>Books/NonFic/Science/Physics</code> would match documents   * containing<code>Books/NonFic</code>,<code>Books/NonFic/Science</code>,   * or<code>Books/NonFic/Science/Physics</code>, but not   *<code>Books/NonFic/Science/Physics/Theory</code> or   *<code>Books/NonFic/Law</code>.  *</p>  *<pre class="prettyprint">  *&lt;fieldType name="descendent_path" class="solr.TextField"&gt;  *&lt;analyzer type="index"&gt;  *&lt;tokenizer class="solr.KeywordTokenizerFactory" /&gt;  *&lt;/analyzer&gt;  *&lt;analyzer type="query"&gt;  *&lt;tokenizer class="solr.PathHierarchyTokenizerFactory" delimiter="/" /&gt;  *&lt;/analyzer&gt;  *&lt;/fieldType&gt;  *</pre>  */
end_comment

begin_class
DECL|class|PathHierarchyTokenizerFactory
specifier|public
class|class
name|PathHierarchyTokenizerFactory
extends|extends
name|TokenizerFactory
block|{
DECL|field|delimiter
specifier|private
name|char
name|delimiter
decl_stmt|;
DECL|field|replacement
specifier|private
name|char
name|replacement
decl_stmt|;
DECL|field|reverse
specifier|private
name|boolean
name|reverse
init|=
literal|false
decl_stmt|;
DECL|field|skip
specifier|private
name|int
name|skip
init|=
name|PathHierarchyTokenizer
operator|.
name|DEFAULT_SKIP
decl_stmt|;
comment|/**    * Require a configured pattern    */
annotation|@
name|Override
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
parameter_list|)
block|{
name|super
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|String
name|v
init|=
name|args
operator|.
name|get
argument_list|(
literal|"delimiter"
argument_list|)
decl_stmt|;
if|if
condition|(
name|v
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|v
operator|.
name|length
argument_list|()
operator|!=
literal|1
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"delimiter should be a char. \""
operator|+
name|v
operator|+
literal|"\" is invalid"
argument_list|)
throw|;
block|}
else|else
block|{
name|delimiter
operator|=
name|v
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|delimiter
operator|=
name|PathHierarchyTokenizer
operator|.
name|DEFAULT_DELIMITER
expr_stmt|;
block|}
name|v
operator|=
name|args
operator|.
name|get
argument_list|(
literal|"replace"
argument_list|)
expr_stmt|;
if|if
condition|(
name|v
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|v
operator|.
name|length
argument_list|()
operator|!=
literal|1
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"replace should be a char. \""
operator|+
name|v
operator|+
literal|"\" is invalid"
argument_list|)
throw|;
block|}
else|else
block|{
name|replacement
operator|=
name|v
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|replacement
operator|=
name|delimiter
expr_stmt|;
block|}
name|v
operator|=
name|args
operator|.
name|get
argument_list|(
literal|"reverse"
argument_list|)
expr_stmt|;
if|if
condition|(
name|v
operator|!=
literal|null
condition|)
block|{
name|reverse
operator|=
literal|"true"
operator|.
name|equals
argument_list|(
name|v
argument_list|)
expr_stmt|;
block|}
name|v
operator|=
name|args
operator|.
name|get
argument_list|(
literal|"skip"
argument_list|)
expr_stmt|;
if|if
condition|(
name|v
operator|!=
literal|null
condition|)
block|{
name|skip
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|v
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|create
specifier|public
name|Tokenizer
name|create
parameter_list|(
name|Reader
name|input
parameter_list|)
block|{
if|if
condition|(
name|reverse
condition|)
block|{
return|return
operator|new
name|ReversePathHierarchyTokenizer
argument_list|(
name|input
argument_list|,
name|delimiter
argument_list|,
name|replacement
argument_list|,
name|skip
argument_list|)
return|;
block|}
return|return
operator|new
name|PathHierarchyTokenizer
argument_list|(
name|input
argument_list|,
name|delimiter
argument_list|,
name|replacement
argument_list|,
name|skip
argument_list|)
return|;
block|}
block|}
end_class

end_unit

