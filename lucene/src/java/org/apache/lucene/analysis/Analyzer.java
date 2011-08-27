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
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Modifier
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
name|index
operator|.
name|IndexableField
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
name|CloseableThreadLocal
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
name|store
operator|.
name|AlreadyClosedException
import|;
end_import

begin_comment
comment|/** An Analyzer builds TokenStreams, which analyze text.  It thus represents a  *  policy for extracting index terms from text.  *<p>  *  Typical implementations first build a Tokenizer, which breaks the stream of  *  characters from the Reader into raw Tokens.  One or more TokenFilters may  *  then be applied to the output of the Tokenizer.  *<p>The {@code Analyzer}-API in Lucene is based on the decorator pattern.  * Therefore all non-abstract subclasses must be final or their {@link #tokenStream}  * and {@link #reusableTokenStream} implementations must be final! This is checked  * when Java assertions are enabled.  */
end_comment

begin_class
DECL|class|Analyzer
specifier|public
specifier|abstract
class|class
name|Analyzer
implements|implements
name|Closeable
block|{
DECL|method|Analyzer
specifier|protected
name|Analyzer
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
assert|assert
name|assertFinal
argument_list|()
assert|;
block|}
DECL|method|assertFinal
specifier|private
name|boolean
name|assertFinal
parameter_list|()
block|{
try|try
block|{
specifier|final
name|Class
argument_list|<
name|?
argument_list|>
name|clazz
init|=
name|getClass
argument_list|()
decl_stmt|;
assert|assert
name|clazz
operator|.
name|isAnonymousClass
argument_list|()
operator|||
operator|(
name|clazz
operator|.
name|getModifiers
argument_list|()
operator|&
operator|(
name|Modifier
operator|.
name|FINAL
operator||
name|Modifier
operator|.
name|PRIVATE
operator|)
operator|)
operator|!=
literal|0
operator|||
operator|(
name|Modifier
operator|.
name|isFinal
argument_list|(
name|clazz
operator|.
name|getMethod
argument_list|(
literal|"tokenStream"
argument_list|,
name|String
operator|.
name|class
argument_list|,
name|Reader
operator|.
name|class
argument_list|)
operator|.
name|getModifiers
argument_list|()
argument_list|)
operator|&&
name|Modifier
operator|.
name|isFinal
argument_list|(
name|clazz
operator|.
name|getMethod
argument_list|(
literal|"reusableTokenStream"
argument_list|,
name|String
operator|.
name|class
argument_list|,
name|Reader
operator|.
name|class
argument_list|)
operator|.
name|getModifiers
argument_list|()
argument_list|)
operator|)
operator|:
literal|"Analyzer implementation classes or at least their tokenStream() and reusableTokenStream() implementations must be final"
assert|;
return|return
literal|true
return|;
block|}
catch|catch
parameter_list|(
name|NoSuchMethodException
name|nsme
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
block|}
comment|/** Creates a TokenStream which tokenizes all the text in the provided    * Reader.  Must be able to handle null field name for    * backward compatibility.    */
DECL|method|tokenStream
specifier|public
specifier|abstract
name|TokenStream
name|tokenStream
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Reader
name|reader
parameter_list|)
function_decl|;
comment|/** Creates a TokenStream that is allowed to be re-used    *  from the previous time that the same thread called    *  this method.  Callers that do not need to use more    *  than one TokenStream at the same time from this    *  analyzer should use this method for better    *  performance.    */
DECL|method|reusableTokenStream
specifier|public
name|TokenStream
name|reusableTokenStream
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Reader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|tokenStream
argument_list|(
name|fieldName
argument_list|,
name|reader
argument_list|)
return|;
block|}
DECL|field|tokenStreams
specifier|private
name|CloseableThreadLocal
argument_list|<
name|Object
argument_list|>
name|tokenStreams
init|=
operator|new
name|CloseableThreadLocal
argument_list|<
name|Object
argument_list|>
argument_list|()
decl_stmt|;
comment|/** Used by Analyzers that implement reusableTokenStream    *  to retrieve previously saved TokenStreams for re-use    *  by the same thread. */
DECL|method|getPreviousTokenStream
specifier|protected
name|Object
name|getPreviousTokenStream
parameter_list|()
block|{
try|try
block|{
return|return
name|tokenStreams
operator|.
name|get
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|NullPointerException
name|npe
parameter_list|)
block|{
if|if
condition|(
name|tokenStreams
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|AlreadyClosedException
argument_list|(
literal|"this Analyzer is closed"
argument_list|)
throw|;
block|}
else|else
block|{
throw|throw
name|npe
throw|;
block|}
block|}
block|}
comment|/** Used by Analyzers that implement reusableTokenStream    *  to save a TokenStream for later re-use by the same    *  thread. */
DECL|method|setPreviousTokenStream
specifier|protected
name|void
name|setPreviousTokenStream
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
try|try
block|{
name|tokenStreams
operator|.
name|set
argument_list|(
name|obj
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NullPointerException
name|npe
parameter_list|)
block|{
if|if
condition|(
name|tokenStreams
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|AlreadyClosedException
argument_list|(
literal|"this Analyzer is closed"
argument_list|)
throw|;
block|}
else|else
block|{
throw|throw
name|npe
throw|;
block|}
block|}
block|}
comment|/**    * Invoked before indexing a IndexableField instance if    * terms have already been added to that field.  This allows custom    * analyzers to place an automatic position increment gap between    * IndexbleField instances using the same field name.  The default value    * position increment gap is 0.  With a 0 position increment gap and    * the typical default token position increment of 1, all terms in a field,    * including across IndexableField instances, are in successive positions, allowing    * exact PhraseQuery matches, for instance, across IndexableField instance boundaries.    *    * @param fieldName IndexableField name being indexed.    * @return position increment gap, added to the next token emitted from {@link #tokenStream(String,Reader)}    */
DECL|method|getPositionIncrementGap
specifier|public
name|int
name|getPositionIncrementGap
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
return|return
literal|0
return|;
block|}
comment|/**    * Just like {@link #getPositionIncrementGap}, except for    * Token offsets instead.  By default this returns 1 for    * tokenized fields and, as if the fields were joined    * with an extra space character, and 0 for un-tokenized    * fields.  This method is only called if the field    * produced at least one token for indexing.    *    * @param field the field just indexed    * @return offset gap, added to the next token emitted from {@link #tokenStream(String,Reader)}    */
DECL|method|getOffsetGap
specifier|public
name|int
name|getOffsetGap
parameter_list|(
name|IndexableField
name|field
parameter_list|)
block|{
if|if
condition|(
name|field
operator|.
name|tokenized
argument_list|()
condition|)
block|{
return|return
literal|1
return|;
block|}
else|else
block|{
return|return
literal|0
return|;
block|}
block|}
comment|/** Frees persistent resources used by this Analyzer */
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{
name|tokenStreams
operator|.
name|close
argument_list|()
expr_stmt|;
name|tokenStreams
operator|=
literal|null
expr_stmt|;
block|}
block|}
end_class

end_unit

