begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
package|;
end_package

begin_comment
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|Set
import|;
end_import

begin_comment
comment|/** Filters LetterTokenizer with LowerCaseFilter and StopFilter. */
end_comment

begin_class
DECL|class|StopAnalyzer
specifier|public
specifier|final
class|class
name|StopAnalyzer
extends|extends
name|Analyzer
block|{
DECL|field|stopWords
specifier|private
name|Set
name|stopWords
decl_stmt|;
comment|/** An array containing some common English words that are not usually useful     for searching. */
DECL|field|ENGLISH_STOP_WORDS
specifier|public
specifier|static
specifier|final
name|String
index|[]
name|ENGLISH_STOP_WORDS
init|=
block|{
literal|"a"
block|,
literal|"an"
block|,
literal|"and"
block|,
literal|"are"
block|,
literal|"as"
block|,
literal|"at"
block|,
literal|"be"
block|,
literal|"but"
block|,
literal|"by"
block|,
literal|"for"
block|,
literal|"if"
block|,
literal|"in"
block|,
literal|"into"
block|,
literal|"is"
block|,
literal|"it"
block|,
literal|"no"
block|,
literal|"not"
block|,
literal|"of"
block|,
literal|"on"
block|,
literal|"or"
block|,
literal|"s"
block|,
literal|"such"
block|,
literal|"t"
block|,
literal|"that"
block|,
literal|"the"
block|,
literal|"their"
block|,
literal|"then"
block|,
literal|"there"
block|,
literal|"these"
block|,
literal|"they"
block|,
literal|"this"
block|,
literal|"to"
block|,
literal|"was"
block|,
literal|"will"
block|,
literal|"with"
block|}
decl_stmt|;
comment|/** Builds an analyzer which removes words in ENGLISH_STOP_WORDS. */
DECL|method|StopAnalyzer
specifier|public
name|StopAnalyzer
parameter_list|()
block|{
name|stopWords
operator|=
name|StopFilter
operator|.
name|makeStopSet
argument_list|(
name|ENGLISH_STOP_WORDS
argument_list|)
expr_stmt|;
block|}
comment|/** Builds an analyzer which removes words in the provided array. */
DECL|method|StopAnalyzer
specifier|public
name|StopAnalyzer
parameter_list|(
name|String
index|[]
name|stopWords
parameter_list|)
block|{
name|this
operator|.
name|stopWords
operator|=
name|StopFilter
operator|.
name|makeStopSet
argument_list|(
name|stopWords
argument_list|)
expr_stmt|;
block|}
comment|/** Filters LowerCaseTokenizer with StopFilter. */
DECL|method|tokenStream
specifier|public
name|TokenStream
name|tokenStream
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Reader
name|reader
parameter_list|)
block|{
return|return
operator|new
name|StopFilter
argument_list|(
operator|new
name|LowerCaseTokenizer
argument_list|(
name|reader
argument_list|)
argument_list|,
name|stopWords
argument_list|)
return|;
block|}
block|}
end_class

end_unit

