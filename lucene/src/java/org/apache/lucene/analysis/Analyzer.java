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
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|store
operator|.
name|AlreadyClosedException
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
name|Reader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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

begin_comment
comment|/**  * An Analyzer builds TokenStreams, which analyze text.  It thus represents a  * policy for extracting index terms from text.  *<p>  * In order to define what analysis is done, subclasses must define their  * {@link TokenStreamComponents} in {@link #createComponents(String, Reader)}.  * The components are then reused in each call to {@link #tokenStream(String, Reader)}.  */
end_comment

begin_class
DECL|class|Analyzer
specifier|public
specifier|abstract
class|class
name|Analyzer
block|{
DECL|field|reuseStrategy
specifier|private
specifier|final
name|ReuseStrategy
name|reuseStrategy
decl_stmt|;
DECL|method|Analyzer
specifier|public
name|Analyzer
parameter_list|()
block|{
name|this
argument_list|(
operator|new
name|GlobalReuseStrategy
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|Analyzer
specifier|public
name|Analyzer
parameter_list|(
name|ReuseStrategy
name|reuseStrategy
parameter_list|)
block|{
name|this
operator|.
name|reuseStrategy
operator|=
name|reuseStrategy
expr_stmt|;
block|}
comment|/**    * Creates a new {@link TokenStreamComponents} instance for this analyzer.    *     * @param fieldName    *          the name of the fields content passed to the    *          {@link TokenStreamComponents} sink as a reader    * @param reader    *          the reader passed to the {@link Tokenizer} constructor    * @return the {@link TokenStreamComponents} for this analyzer.    */
DECL|method|createComponents
specifier|protected
specifier|abstract
name|TokenStreamComponents
name|createComponents
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Reader
name|reader
parameter_list|)
function_decl|;
comment|/**    * Creates a TokenStream that is allowed to be re-use from the previous time    * that the same thread called this method.  Callers that do not need to use    * more than one TokenStream at the same time from this analyzer should use    * this method for better performance.    *<p>    * This method uses {@link #createComponents(String, Reader)} to obtain an    * instance of {@link TokenStreamComponents}. It returns the sink of the    * components and stores the components internally. Subsequent calls to this    * method will reuse the previously stored components after resetting them    * through {@link TokenStreamComponents#reset(Reader)}.    *</p>    *     * @param fieldName the name of the field the created TokenStream is used for    * @param reader the reader the streams source reads from    */
DECL|method|tokenStream
specifier|public
specifier|final
name|TokenStream
name|tokenStream
parameter_list|(
specifier|final
name|String
name|fieldName
parameter_list|,
specifier|final
name|Reader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
name|TokenStreamComponents
name|components
init|=
name|reuseStrategy
operator|.
name|getReusableComponents
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
specifier|final
name|Reader
name|r
init|=
name|initReader
argument_list|(
name|reader
argument_list|)
decl_stmt|;
if|if
condition|(
name|components
operator|==
literal|null
condition|)
block|{
name|components
operator|=
name|createComponents
argument_list|(
name|fieldName
argument_list|,
name|r
argument_list|)
expr_stmt|;
name|reuseStrategy
operator|.
name|setReusableComponents
argument_list|(
name|fieldName
argument_list|,
name|components
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|components
operator|.
name|reset
argument_list|(
name|r
argument_list|)
expr_stmt|;
block|}
return|return
name|components
operator|.
name|getTokenStream
argument_list|()
return|;
block|}
comment|/**    * Override this if you want to add a CharFilter chain.    */
DECL|method|initReader
specifier|protected
name|Reader
name|initReader
parameter_list|(
name|Reader
name|reader
parameter_list|)
block|{
return|return
name|reader
return|;
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
name|fieldType
argument_list|()
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
name|reuseStrategy
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**    * This class encapsulates the outer components of a token stream. It provides    * access to the source ({@link Tokenizer}) and the outer end (sink), an    * instance of {@link TokenFilter} which also serves as the    * {@link TokenStream} returned by    * {@link Analyzer#tokenStream(String, Reader)}.    */
DECL|class|TokenStreamComponents
specifier|public
specifier|static
class|class
name|TokenStreamComponents
block|{
DECL|field|source
specifier|protected
specifier|final
name|Tokenizer
name|source
decl_stmt|;
DECL|field|sink
specifier|protected
specifier|final
name|TokenStream
name|sink
decl_stmt|;
comment|/**      * Creates a new {@link TokenStreamComponents} instance.      *       * @param source      *          the analyzer's tokenizer      * @param result      *          the analyzer's resulting token stream      */
DECL|method|TokenStreamComponents
specifier|public
name|TokenStreamComponents
parameter_list|(
specifier|final
name|Tokenizer
name|source
parameter_list|,
specifier|final
name|TokenStream
name|result
parameter_list|)
block|{
name|this
operator|.
name|source
operator|=
name|source
expr_stmt|;
name|this
operator|.
name|sink
operator|=
name|result
expr_stmt|;
block|}
comment|/**      * Creates a new {@link TokenStreamComponents} instance.      *       * @param source      *          the analyzer's tokenizer      */
DECL|method|TokenStreamComponents
specifier|public
name|TokenStreamComponents
parameter_list|(
specifier|final
name|Tokenizer
name|source
parameter_list|)
block|{
name|this
operator|.
name|source
operator|=
name|source
expr_stmt|;
name|this
operator|.
name|sink
operator|=
name|source
expr_stmt|;
block|}
comment|/**      * Resets the encapsulated components with the given reader. If the components      * cannot be reset, an Exception should be thrown.      *       * @param reader      *          a reader to reset the source component      * @throws IOException      *           if the component's reset method throws an {@link IOException}      */
DECL|method|reset
specifier|protected
name|void
name|reset
parameter_list|(
specifier|final
name|Reader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
name|source
operator|.
name|reset
argument_list|(
name|reader
argument_list|)
expr_stmt|;
block|}
comment|/**      * Returns the sink {@link TokenStream}      *       * @return the sink {@link TokenStream}      */
DECL|method|getTokenStream
specifier|public
name|TokenStream
name|getTokenStream
parameter_list|()
block|{
return|return
name|sink
return|;
block|}
comment|/**      * Returns the component's {@link Tokenizer}      *      * @return Component's {@link Tokenizer}      */
DECL|method|getTokenizer
specifier|public
name|Tokenizer
name|getTokenizer
parameter_list|()
block|{
return|return
name|source
return|;
block|}
block|}
comment|/**    * Strategy defining how TokenStreamComponents are reused per call to    * {@link Analyzer#tokenStream(String, java.io.Reader)}.    */
DECL|class|ReuseStrategy
specifier|public
specifier|static
specifier|abstract
class|class
name|ReuseStrategy
block|{
DECL|field|storedValue
specifier|private
name|CloseableThreadLocal
argument_list|<
name|Object
argument_list|>
name|storedValue
init|=
operator|new
name|CloseableThreadLocal
argument_list|<
name|Object
argument_list|>
argument_list|()
decl_stmt|;
comment|/**      * Gets the reusable TokenStreamComponents for the field with the given name      *      * @param fieldName Name of the field whose reusable TokenStreamComponents      *        are to be retrieved      * @return Reusable TokenStreamComponents for the field, or {@code null}      *         if there was no previous components for the field      */
DECL|method|getReusableComponents
specifier|public
specifier|abstract
name|TokenStreamComponents
name|getReusableComponents
parameter_list|(
name|String
name|fieldName
parameter_list|)
function_decl|;
comment|/**      * Stores the given TokenStreamComponents as the reusable components for the      * field with the give name      *      * @param fieldName Name of the field whose TokenStreamComponents are being set      * @param components TokenStreamComponents which are to be reused for the field      */
DECL|method|setReusableComponents
specifier|public
specifier|abstract
name|void
name|setReusableComponents
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|TokenStreamComponents
name|components
parameter_list|)
function_decl|;
comment|/**      * Returns the currently stored value      *      * @return Currently stored value or {@code null} if no value is stored      */
DECL|method|getStoredValue
specifier|protected
specifier|final
name|Object
name|getStoredValue
parameter_list|()
block|{
try|try
block|{
return|return
name|storedValue
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
name|storedValue
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
comment|/**      * Sets the stored value      *      * @param storedValue Value to store      */
DECL|method|setStoredValue
specifier|protected
specifier|final
name|void
name|setStoredValue
parameter_list|(
name|Object
name|storedValue
parameter_list|)
block|{
try|try
block|{
name|this
operator|.
name|storedValue
operator|.
name|set
argument_list|(
name|storedValue
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
name|storedValue
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
comment|/**      * Closes the ReuseStrategy, freeing any resources      */
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{
name|storedValue
operator|.
name|close
argument_list|()
expr_stmt|;
name|storedValue
operator|=
literal|null
expr_stmt|;
block|}
block|}
comment|/**    * Implementation of {@link ReuseStrategy} that reuses the same components for    * every field.    */
DECL|class|GlobalReuseStrategy
specifier|public
specifier|final
specifier|static
class|class
name|GlobalReuseStrategy
extends|extends
name|ReuseStrategy
block|{
comment|/**      * {@inheritDoc}      */
DECL|method|getReusableComponents
specifier|public
name|TokenStreamComponents
name|getReusableComponents
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
return|return
operator|(
name|TokenStreamComponents
operator|)
name|getStoredValue
argument_list|()
return|;
block|}
comment|/**      * {@inheritDoc}      */
DECL|method|setReusableComponents
specifier|public
name|void
name|setReusableComponents
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|TokenStreamComponents
name|components
parameter_list|)
block|{
name|setStoredValue
argument_list|(
name|components
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Implementation of {@link ReuseStrategy} that reuses components per-field by    * maintaining a Map of TokenStreamComponent per field name.    */
DECL|class|PerFieldReuseStrategy
specifier|public
specifier|static
class|class
name|PerFieldReuseStrategy
extends|extends
name|ReuseStrategy
block|{
comment|/**      * {@inheritDoc}      */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|getReusableComponents
specifier|public
name|TokenStreamComponents
name|getReusableComponents
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|TokenStreamComponents
argument_list|>
name|componentsPerField
init|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|TokenStreamComponents
argument_list|>
operator|)
name|getStoredValue
argument_list|()
decl_stmt|;
return|return
name|componentsPerField
operator|!=
literal|null
condition|?
name|componentsPerField
operator|.
name|get
argument_list|(
name|fieldName
argument_list|)
else|:
literal|null
return|;
block|}
comment|/**      * {@inheritDoc}      */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|setReusableComponents
specifier|public
name|void
name|setReusableComponents
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|TokenStreamComponents
name|components
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|TokenStreamComponents
argument_list|>
name|componentsPerField
init|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|TokenStreamComponents
argument_list|>
operator|)
name|getStoredValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|componentsPerField
operator|==
literal|null
condition|)
block|{
name|componentsPerField
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|TokenStreamComponents
argument_list|>
argument_list|()
expr_stmt|;
name|setStoredValue
argument_list|(
name|componentsPerField
argument_list|)
expr_stmt|;
block|}
name|componentsPerField
operator|.
name|put
argument_list|(
name|fieldName
argument_list|,
name|components
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

