begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.analysis
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|analysis
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|analysis
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * When the plain text is extracted from documents, we will often have many words hyphenated and broken into  * two lines. This is often the case with documents where narrow text columns are used, such as newsletters.  * In order to increase search efficiency, this filter puts hyphenated words broken into two lines back together.  * This filter should be used on indexing time only.  * Example field definition in schema.xml:  *<pre>  *<fieldtype name="text" class="solr.TextField" positionIncrementGap="100">  *<analyzer type="index">  *<tokenizer class="solr.WhitespaceTokenizerFactory"/>  *<filter class="solr.SynonymFilterFactory" synonyms="index_synonyms.txt" ignoreCase="true" expand="false"/>  *<filter class="solr.StopFilterFactory" ignoreCase="true"/>  *<filter class="solr.HyphenatedWordsFilterFactory"/>  *<filter class="solr.WordDelimiterFilterFactory" generateWordParts="1" generateNumberParts="1" catenateWords="1" catenateNumbers="1" catenateAll="0"/>  *<filter class="solr.LowerCaseFilterFactory"/>  *<filter class="solr.RemoveDuplicatesTokenFilterFactory"/>  *</analyzer>  *<analyzer type="query">  *<tokenizer class="solr.WhitespaceTokenizerFactory"/>  *<filter class="solr.SynonymFilterFactory" synonyms="synonyms.txt" ignoreCase="true" expand="true"/>  *<filter class="solr.StopFilterFactory" ignoreCase="true"/>  *<filter class="solr.WordDelimiterFilterFactory" generateWordParts="1" generateNumberParts="1" catenateWords="0" catenateNumbers="0" catenateAll="0"/>  *<filter class="solr.LowerCaseFilterFactory"/>  *<filter class="solr.RemoveDuplicatesTokenFilterFactory"/>  *</analyzer>  *</fieldtype>  *   */
end_comment

begin_class
DECL|class|HyphenatedWordsFilter
specifier|public
specifier|final
class|class
name|HyphenatedWordsFilter
extends|extends
name|TokenFilter
block|{
DECL|method|HyphenatedWordsFilter
specifier|public
name|HyphenatedWordsFilter
parameter_list|(
name|TokenStream
name|in
parameter_list|)
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * @inheritDoc 	 * @see org.apache.lucene.analysis.TokenStream#next() 	 */
DECL|method|next
specifier|public
specifier|final
name|Token
name|next
parameter_list|()
throws|throws
name|IOException
block|{
name|StringBuffer
name|termText
init|=
operator|new
name|StringBuffer
argument_list|(
literal|25
argument_list|)
decl_stmt|;
name|int
name|startOffset
init|=
operator|-
literal|1
decl_stmt|,
name|firstPositionIncrement
init|=
operator|-
literal|1
decl_stmt|,
name|wordsMerged
init|=
literal|0
decl_stmt|;
name|Token
name|lastToken
init|=
literal|null
decl_stmt|;
for|for
control|(
name|Token
name|token
init|=
name|input
operator|.
name|next
argument_list|()
init|;
name|token
operator|!=
literal|null
condition|;
name|token
operator|=
name|input
operator|.
name|next
argument_list|()
control|)
block|{
name|termText
operator|.
name|append
argument_list|(
name|token
operator|.
name|termText
argument_list|()
argument_list|)
expr_stmt|;
comment|//current token ends with hyphen -> grab the next token and glue them together
if|if
condition|(
name|termText
operator|.
name|charAt
argument_list|(
name|termText
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
operator|==
literal|'-'
condition|)
block|{
name|wordsMerged
operator|++
expr_stmt|;
comment|//remove the hyphen
name|termText
operator|.
name|setLength
argument_list|(
name|termText
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
if|if
condition|(
name|startOffset
operator|==
operator|-
literal|1
condition|)
block|{
name|startOffset
operator|=
name|token
operator|.
name|startOffset
argument_list|()
expr_stmt|;
name|firstPositionIncrement
operator|=
name|token
operator|.
name|getPositionIncrement
argument_list|()
expr_stmt|;
block|}
name|lastToken
operator|=
name|token
expr_stmt|;
block|}
else|else
block|{
comment|//shortcut returns token
if|if
condition|(
name|wordsMerged
operator|==
literal|0
condition|)
return|return
name|token
return|;
name|Token
name|mergedToken
init|=
operator|new
name|Token
argument_list|(
name|termText
operator|.
name|toString
argument_list|()
argument_list|,
name|startOffset
argument_list|,
name|token
operator|.
name|endOffset
argument_list|()
argument_list|,
name|token
operator|.
name|type
argument_list|()
argument_list|)
decl_stmt|;
name|mergedToken
operator|.
name|setPositionIncrement
argument_list|(
name|firstPositionIncrement
argument_list|)
expr_stmt|;
return|return
name|mergedToken
return|;
block|}
block|}
comment|//last token ending with hyphen? - we know that we have only one token in
comment|//this situation, so we can safely return firstToken
if|if
condition|(
name|startOffset
operator|!=
operator|-
literal|1
condition|)
return|return
name|lastToken
return|;
else|else
return|return
literal|null
return|;
comment|//end of token stream
block|}
block|}
end_class

end_unit

