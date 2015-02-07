begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.en
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|en
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
name|TokenFilter
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
name|TokenStream
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
name|tokenattributes
operator|.
name|CharTermAttribute
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
name|tokenattributes
operator|.
name|KeywordAttribute
import|;
end_import

begin_comment
comment|/** A high-performance kstem filter for english.  *<p>  * See<a href="http://ciir.cs.umass.edu/pubfiles/ir-35.pdf">  * "Viewing Morphology as an Inference Process"</a>  * (Krovetz, R., Proceedings of the Sixteenth Annual International ACM SIGIR  * Conference on Research and Development in Information Retrieval, 191-203, 1993).  *<p>  * All terms must already be lowercased for this filter to work correctly.  *  *<p>  * Note: This filter is aware of the {@link KeywordAttribute}. To prevent  * certain terms from being passed to the stemmer  * {@link KeywordAttribute#isKeyword()} should be set to<code>true</code>  * in a previous {@link TokenStream}.  *  * Note: For including the original term as well as the stemmed version, see  * {@link org.apache.lucene.analysis.miscellaneous.KeywordRepeatFilterFactory}  *</p>  *  *  */
end_comment

begin_class
DECL|class|KStemFilter
specifier|public
specifier|final
class|class
name|KStemFilter
extends|extends
name|TokenFilter
block|{
DECL|field|stemmer
specifier|private
specifier|final
name|KStemmer
name|stemmer
init|=
operator|new
name|KStemmer
argument_list|()
decl_stmt|;
DECL|field|termAttribute
specifier|private
specifier|final
name|CharTermAttribute
name|termAttribute
init|=
name|addAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|keywordAtt
specifier|private
specifier|final
name|KeywordAttribute
name|keywordAtt
init|=
name|addAttribute
argument_list|(
name|KeywordAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|KStemFilter
specifier|public
name|KStemFilter
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
comment|/** Returns the next, stemmed, input Token.    *  @return The stemmed form of a token.    *  @throws IOException If there is a low-level I/O error.    */
annotation|@
name|Override
DECL|method|incrementToken
specifier|public
name|boolean
name|incrementToken
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|input
operator|.
name|incrementToken
argument_list|()
condition|)
return|return
literal|false
return|;
name|char
index|[]
name|term
init|=
name|termAttribute
operator|.
name|buffer
argument_list|()
decl_stmt|;
name|int
name|len
init|=
name|termAttribute
operator|.
name|length
argument_list|()
decl_stmt|;
if|if
condition|(
operator|(
operator|!
name|keywordAtt
operator|.
name|isKeyword
argument_list|()
operator|)
operator|&&
name|stemmer
operator|.
name|stem
argument_list|(
name|term
argument_list|,
name|len
argument_list|)
condition|)
block|{
name|termAttribute
operator|.
name|setEmpty
argument_list|()
operator|.
name|append
argument_list|(
name|stemmer
operator|.
name|asCharSequence
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

