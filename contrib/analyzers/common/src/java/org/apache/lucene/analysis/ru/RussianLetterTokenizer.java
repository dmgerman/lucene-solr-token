begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.ru
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|ru
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
name|Reader
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
name|CharTokenizer
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

begin_comment
comment|// for javadocs
end_comment

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
name|LetterTokenizer
import|;
end_import

begin_comment
comment|// for javadocs
end_comment

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
name|AttributeSource
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
name|Version
import|;
end_import

begin_comment
comment|/**  * A RussianLetterTokenizer is a {@link Tokenizer} that extends {@link LetterTokenizer}  * by also allowing the basic Latin digits 0-9.  *<p>  *<a name="version"/>  * You must specify the required {@link Version} compatibility when creating  * {@link RussianLetterTokenizer}:  *<ul>  *<li>As of 3.1, {@link CharTokenizer} uses an int based API to normalize and  * detect token characters. See {@link CharTokenizer#isTokenChar(int)} and  * {@link CharTokenizer#normalize(int)} for details.</li>  *</ul>   */
end_comment

begin_class
DECL|class|RussianLetterTokenizer
specifier|public
class|class
name|RussianLetterTokenizer
extends|extends
name|CharTokenizer
block|{
DECL|field|DIGIT_0
specifier|private
specifier|static
specifier|final
name|int
name|DIGIT_0
init|=
literal|'0'
decl_stmt|;
DECL|field|DIGIT_9
specifier|private
specifier|static
specifier|final
name|int
name|DIGIT_9
init|=
literal|'9'
decl_stmt|;
comment|/**      * Construct a new RussianLetterTokenizer. * @param matchVersion Lucene version      * to match See {@link<a href="#version">above</a>}      *       * @param in      *          the input to split up into tokens      */
DECL|method|RussianLetterTokenizer
specifier|public
name|RussianLetterTokenizer
parameter_list|(
name|Version
name|matchVersion
parameter_list|,
name|Reader
name|in
parameter_list|)
block|{
name|super
argument_list|(
name|matchVersion
argument_list|,
name|in
argument_list|)
expr_stmt|;
block|}
comment|/**      * Construct a new RussianLetterTokenizer using a given {@link AttributeSource}.      *       * @param matchVersion      *          Lucene version to match See {@link<a href="#version">above</a>}      * @param source      *          the attribute source to use for this {@link Tokenizer}      * @param in      *          the input to split up into tokens      */
DECL|method|RussianLetterTokenizer
specifier|public
name|RussianLetterTokenizer
parameter_list|(
name|Version
name|matchVersion
parameter_list|,
name|AttributeSource
name|source
parameter_list|,
name|Reader
name|in
parameter_list|)
block|{
name|super
argument_list|(
name|matchVersion
argument_list|,
name|source
argument_list|,
name|in
argument_list|)
expr_stmt|;
block|}
comment|/**      * Construct a new RussianLetterTokenizer using a given      * {@link org.apache.lucene.util.AttributeSource.AttributeFactory}. * @param      * matchVersion Lucene version to match See      * {@link<a href="#version">above</a>}      *       * @param factory      *          the attribute factory to use for this {@link Tokenizer}      * @param in      *          the input to split up into tokens      */
DECL|method|RussianLetterTokenizer
specifier|public
name|RussianLetterTokenizer
parameter_list|(
name|Version
name|matchVersion
parameter_list|,
name|AttributeFactory
name|factory
parameter_list|,
name|Reader
name|in
parameter_list|)
block|{
name|super
argument_list|(
name|matchVersion
argument_list|,
name|factory
argument_list|,
name|in
argument_list|)
expr_stmt|;
block|}
comment|/**      * Construct a new RussianLetterTokenizer.      *       * @deprecated use {@link #RussianLetterTokenizer(Version, Reader)} instead. This will      *             be removed in Lucene 4.0.      */
annotation|@
name|Deprecated
DECL|method|RussianLetterTokenizer
specifier|public
name|RussianLetterTokenizer
parameter_list|(
name|Reader
name|in
parameter_list|)
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
comment|/**      * Construct a new RussianLetterTokenizer using a given {@link AttributeSource}.      *       * @deprecated use {@link #RussianLetterTokenizer(Version, AttributeSource, Reader)}      *             instead. This will be removed in Lucene 4.0.      */
annotation|@
name|Deprecated
DECL|method|RussianLetterTokenizer
specifier|public
name|RussianLetterTokenizer
parameter_list|(
name|AttributeSource
name|source
parameter_list|,
name|Reader
name|in
parameter_list|)
block|{
name|super
argument_list|(
name|source
argument_list|,
name|in
argument_list|)
expr_stmt|;
block|}
comment|/**      * Construct a new RussianLetterTokenizer using a given      * {@link org.apache.lucene.util.AttributeSource.AttributeFactory}.      *       * @deprecated use {@link #RussianLetterTokenizer(Version, AttributeSource.AttributeFactory, Reader)}      *             instead. This will be removed in Lucene 4.0.      */
annotation|@
name|Deprecated
DECL|method|RussianLetterTokenizer
specifier|public
name|RussianLetterTokenizer
parameter_list|(
name|AttributeFactory
name|factory
parameter_list|,
name|Reader
name|in
parameter_list|)
block|{
name|super
argument_list|(
name|factory
argument_list|,
name|in
argument_list|)
expr_stmt|;
block|}
comment|/**      * Collects only characters which satisfy      * {@link Character#isLetter(int)}.      */
annotation|@
name|Override
DECL|method|isTokenChar
specifier|protected
name|boolean
name|isTokenChar
parameter_list|(
name|int
name|c
parameter_list|)
block|{
return|return
name|Character
operator|.
name|isLetter
argument_list|(
name|c
argument_list|)
operator|||
operator|(
name|c
operator|>=
name|DIGIT_0
operator|&&
name|c
operator|<=
name|DIGIT_9
operator|)
return|;
block|}
block|}
end_class

end_unit

