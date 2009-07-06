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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|NumericUtils
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
name|document
operator|.
name|NumericField
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
name|search
operator|.
name|NumericRangeQuery
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
name|search
operator|.
name|NumericRangeFilter
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
name|search
operator|.
name|SortField
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
name|search
operator|.
name|FieldCache
import|;
end_import

begin_comment
comment|// javadocs
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
name|tokenattributes
operator|.
name|TermAttribute
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
name|TypeAttribute
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
name|PositionIncrementAttribute
import|;
end_import

begin_comment
comment|/**  *<b>Expert:</b> This class provides a {@link TokenStream} for indexing numeric values  * that can be used by {@link NumericRangeQuery}/{@link NumericRangeFilter}.  * For more information, how to use this class and its configuration properties  * (<a href="../search/NumericRangeQuery.html#precisionStepDesc"><code>precisionStep</code></a>)  * read the docs of {@link NumericRangeQuery}.  *  *<p><b>For easy usage during indexing, there is a {@link NumericField}, that uses the optimal  * indexing settings (no norms, no term freqs). {@link NumericField} is a wrapper around this  * expert token stream.</b>  *  *<p>This stream is not intended to be used in analyzers, its more for iterating the  * different precisions during indexing a specific numeric value.  * A numeric value is indexed as multiple string encoded terms, each reduced  * by zeroing bits from the right. Each value is also prefixed (in the first char) by the  *<code>shift</code> value (number of bits removed) used during encoding.  * The number of bits removed from the right for each trie entry is called  *<code>precisionStep</code> in this API.  *  *<p>The usage pattern is (it is recommened to switch off norms and term frequencies  * for numeric fields; it does not make sense to have them):  *<pre>  *  Field field = new Field(name, new NumericTokenStream(precisionStep).set<em>???</em>Value(value));  *  field.setOmitNorms(true);  *  field.setOmitTermFreqAndPositions(true);  *  document.add(field);  *</pre>  *<p>For optimal performance, re-use the TokenStream and Field instance  * for more than one document:  *<pre>  *<em>// init</em>  *  NumericTokenStream stream = new NumericTokenStream(precisionStep);  *  Field field = new Field(name, stream);  *  field.setOmitNorms(true);  *  field.setOmitTermFreqAndPositions(true);  *  Document document = new Document();  *  document.add(field);  *<em>// use this code to index many documents:</em>  *  stream.set<em>???</em>Value(value1)  *  writer.addDocument(document);  *  stream.set<em>???</em>Value(value2)  *  writer.addDocument(document);  *  ...  *</pre>  *  *<p><em>Please note:</em> Token streams are read, when the document is added to index.  * If you index more than one numeric field, use a separate instance for each.  *  *<p>Values indexed by this stream can be loaded into the {@link FieldCache}  * and can be sorted (use {@link SortField}{@code .TYPE} to specify the correct  * type; {@link SortField#AUTO} does not work with this type of field)  *  *<p><font color="red"><b>NOTE:</b> This API is experimental and  * might change in incompatible ways in the next release.</font>  *  * @since 2.9  */
end_comment

begin_class
DECL|class|NumericTokenStream
specifier|public
specifier|final
class|class
name|NumericTokenStream
extends|extends
name|TokenStream
block|{
comment|/** The full precision 64 bit token gets this token type assigned. */
DECL|field|TOKEN_TYPE_FULL_PREC_64
specifier|public
specifier|static
specifier|final
name|String
name|TOKEN_TYPE_FULL_PREC_64
init|=
literal|"fullPrecNumeric64"
decl_stmt|;
comment|/** The lower precision 64 bit tokens gets this token type assigned. */
DECL|field|TOKEN_TYPE_LOWER_PREC_64
specifier|public
specifier|static
specifier|final
name|String
name|TOKEN_TYPE_LOWER_PREC_64
init|=
literal|"lowerPrecNumeric64"
decl_stmt|;
comment|/** The full precision 32 bit token gets this token type assigned. */
DECL|field|TOKEN_TYPE_FULL_PREC_32
specifier|public
specifier|static
specifier|final
name|String
name|TOKEN_TYPE_FULL_PREC_32
init|=
literal|"fullPrecNumeric32"
decl_stmt|;
comment|/** The lower precision 32 bit tokens gets this token type assigned. */
DECL|field|TOKEN_TYPE_LOWER_PREC_32
specifier|public
specifier|static
specifier|final
name|String
name|TOKEN_TYPE_LOWER_PREC_32
init|=
literal|"lowerPrecNumeric32"
decl_stmt|;
comment|/**    * Creates a token stream for numeric values. The stream is not yet initialized,    * before using set a value using the various set<em>???</em>Value() methods.    */
DECL|method|NumericTokenStream
specifier|public
name|NumericTokenStream
parameter_list|(
specifier|final
name|int
name|precisionStep
parameter_list|)
block|{
name|this
operator|.
name|precisionStep
operator|=
name|precisionStep
expr_stmt|;
name|termAtt
operator|=
operator|(
name|TermAttribute
operator|)
name|addAttribute
argument_list|(
name|TermAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
name|typeAtt
operator|=
operator|(
name|TypeAttribute
operator|)
name|addAttribute
argument_list|(
name|TypeAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
name|posIncrAtt
operator|=
operator|(
name|PositionIncrementAttribute
operator|)
name|addAttribute
argument_list|(
name|PositionIncrementAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
comment|/**    * Initializes the token stream with the supplied<code>long</code> value.    * @param value the value, for which this TokenStream should enumerate tokens.    * @return this instance, because of this you can use it the following way:    *<code>new Field(name, new NumericTokenStream(precisionStep).setLongValue(value))</code>    */
DECL|method|setLongValue
specifier|public
name|NumericTokenStream
name|setLongValue
parameter_list|(
specifier|final
name|long
name|value
parameter_list|)
block|{
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
name|valSize
operator|=
literal|64
expr_stmt|;
name|shift
operator|=
literal|0
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**    * Initializes the token stream with the supplied<code>int</code> value.    * @param value the value, for which this TokenStream should enumerate tokens.    * @return this instance, because of this you can use it the following way:    *<code>new Field(name, new NumericTokenStream(precisionStep).setIntValue(value))</code>    */
DECL|method|setIntValue
specifier|public
name|NumericTokenStream
name|setIntValue
parameter_list|(
specifier|final
name|int
name|value
parameter_list|)
block|{
name|this
operator|.
name|value
operator|=
operator|(
name|long
operator|)
name|value
expr_stmt|;
name|valSize
operator|=
literal|32
expr_stmt|;
name|shift
operator|=
literal|0
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**    * Initializes the token stream with the supplied<code>double</code> value.    * @param value the value, for which this TokenStream should enumerate tokens.    * @return this instance, because of this you can use it the following way:    *<code>new Field(name, new NumericTokenStream(precisionStep).setDoubleValue(value))</code>    */
DECL|method|setDoubleValue
specifier|public
name|NumericTokenStream
name|setDoubleValue
parameter_list|(
specifier|final
name|double
name|value
parameter_list|)
block|{
name|this
operator|.
name|value
operator|=
name|NumericUtils
operator|.
name|doubleToSortableLong
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|valSize
operator|=
literal|64
expr_stmt|;
name|shift
operator|=
literal|0
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**    * Initializes the token stream with the supplied<code>float</code> value.    * @param value the value, for which this TokenStream should enumerate tokens.    * @return this instance, because of this you can use it the following way:    *<code>new Field(name, new NumericTokenStream(precisionStep).setFloatValue(value))</code>    */
DECL|method|setFloatValue
specifier|public
name|NumericTokenStream
name|setFloatValue
parameter_list|(
specifier|final
name|float
name|value
parameter_list|)
block|{
name|this
operator|.
name|value
operator|=
operator|(
name|long
operator|)
name|NumericUtils
operator|.
name|floatToSortableInt
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|valSize
operator|=
literal|32
expr_stmt|;
name|shift
operator|=
literal|0
expr_stmt|;
return|return
name|this
return|;
block|}
comment|// @Override
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|()
block|{
if|if
condition|(
name|valSize
operator|==
literal|0
condition|)
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"call set???Value() before usage"
argument_list|)
throw|;
if|if
condition|(
name|precisionStep
argument_list|<
literal|1
operator|||
name|precisionStep
argument_list|>
name|valSize
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"precisionStep may only be 1.."
operator|+
name|valSize
argument_list|)
throw|;
name|shift
operator|=
literal|0
expr_stmt|;
block|}
comment|// @Override
DECL|method|incrementToken
specifier|public
name|boolean
name|incrementToken
parameter_list|()
block|{
if|if
condition|(
name|valSize
operator|==
literal|0
condition|)
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"call set???Value() before usage"
argument_list|)
throw|;
if|if
condition|(
name|shift
operator|>=
name|valSize
condition|)
return|return
literal|false
return|;
specifier|final
name|char
index|[]
name|buffer
decl_stmt|;
switch|switch
condition|(
name|valSize
condition|)
block|{
case|case
literal|64
case|:
name|buffer
operator|=
name|termAtt
operator|.
name|resizeTermBuffer
argument_list|(
name|NumericUtils
operator|.
name|LONG_BUF_SIZE
argument_list|)
expr_stmt|;
name|termAtt
operator|.
name|setTermLength
argument_list|(
name|NumericUtils
operator|.
name|longToPrefixCoded
argument_list|(
name|value
argument_list|,
name|shift
argument_list|,
name|buffer
argument_list|)
argument_list|)
expr_stmt|;
name|typeAtt
operator|.
name|setType
argument_list|(
operator|(
name|shift
operator|==
literal|0
operator|)
condition|?
name|TOKEN_TYPE_FULL_PREC_64
else|:
name|TOKEN_TYPE_LOWER_PREC_64
argument_list|)
expr_stmt|;
break|break;
case|case
literal|32
case|:
name|buffer
operator|=
name|termAtt
operator|.
name|resizeTermBuffer
argument_list|(
name|NumericUtils
operator|.
name|INT_BUF_SIZE
argument_list|)
expr_stmt|;
name|termAtt
operator|.
name|setTermLength
argument_list|(
name|NumericUtils
operator|.
name|intToPrefixCoded
argument_list|(
operator|(
name|int
operator|)
name|value
argument_list|,
name|shift
argument_list|,
name|buffer
argument_list|)
argument_list|)
expr_stmt|;
name|typeAtt
operator|.
name|setType
argument_list|(
operator|(
name|shift
operator|==
literal|0
operator|)
condition|?
name|TOKEN_TYPE_FULL_PREC_32
else|:
name|TOKEN_TYPE_LOWER_PREC_32
argument_list|)
expr_stmt|;
break|break;
default|default:
comment|// should not happen
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"valSize must be 32 or 64"
argument_list|)
throw|;
block|}
name|posIncrAtt
operator|.
name|setPositionIncrement
argument_list|(
operator|(
name|shift
operator|==
literal|0
operator|)
condition|?
literal|1
else|:
literal|0
argument_list|)
expr_stmt|;
name|shift
operator|+=
name|precisionStep
expr_stmt|;
return|return
literal|true
return|;
block|}
comment|// @Override
comment|/** @deprecated Will be removed in Lucene 3.0 */
DECL|method|next
specifier|public
name|Token
name|next
parameter_list|(
specifier|final
name|Token
name|reusableToken
parameter_list|)
block|{
assert|assert
name|reusableToken
operator|!=
literal|null
assert|;
if|if
condition|(
name|valSize
operator|==
literal|0
condition|)
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"call set???Value() before usage"
argument_list|)
throw|;
if|if
condition|(
name|shift
operator|>=
name|valSize
condition|)
return|return
literal|null
return|;
name|reusableToken
operator|.
name|clear
argument_list|()
expr_stmt|;
specifier|final
name|char
index|[]
name|buffer
decl_stmt|;
switch|switch
condition|(
name|valSize
condition|)
block|{
case|case
literal|64
case|:
name|buffer
operator|=
name|reusableToken
operator|.
name|resizeTermBuffer
argument_list|(
name|NumericUtils
operator|.
name|LONG_BUF_SIZE
argument_list|)
expr_stmt|;
name|reusableToken
operator|.
name|setTermLength
argument_list|(
name|NumericUtils
operator|.
name|longToPrefixCoded
argument_list|(
name|value
argument_list|,
name|shift
argument_list|,
name|buffer
argument_list|)
argument_list|)
expr_stmt|;
name|reusableToken
operator|.
name|setType
argument_list|(
operator|(
name|shift
operator|==
literal|0
operator|)
condition|?
name|TOKEN_TYPE_FULL_PREC_64
else|:
name|TOKEN_TYPE_LOWER_PREC_64
argument_list|)
expr_stmt|;
break|break;
case|case
literal|32
case|:
name|buffer
operator|=
name|reusableToken
operator|.
name|resizeTermBuffer
argument_list|(
name|NumericUtils
operator|.
name|INT_BUF_SIZE
argument_list|)
expr_stmt|;
name|reusableToken
operator|.
name|setTermLength
argument_list|(
name|NumericUtils
operator|.
name|intToPrefixCoded
argument_list|(
operator|(
name|int
operator|)
name|value
argument_list|,
name|shift
argument_list|,
name|buffer
argument_list|)
argument_list|)
expr_stmt|;
name|reusableToken
operator|.
name|setType
argument_list|(
operator|(
name|shift
operator|==
literal|0
operator|)
condition|?
name|TOKEN_TYPE_FULL_PREC_32
else|:
name|TOKEN_TYPE_LOWER_PREC_32
argument_list|)
expr_stmt|;
break|break;
default|default:
comment|// should not happen
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"valSize must be 32 or 64"
argument_list|)
throw|;
block|}
name|reusableToken
operator|.
name|setPositionIncrement
argument_list|(
operator|(
name|shift
operator|==
literal|0
operator|)
condition|?
literal|1
else|:
literal|0
argument_list|)
expr_stmt|;
name|shift
operator|+=
name|precisionStep
expr_stmt|;
return|return
name|reusableToken
return|;
block|}
comment|// @Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
specifier|final
name|StringBuffer
name|sb
init|=
operator|new
name|StringBuffer
argument_list|(
literal|"(numeric,valSize="
argument_list|)
operator|.
name|append
argument_list|(
name|valSize
argument_list|)
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|",precisionStep="
argument_list|)
operator|.
name|append
argument_list|(
name|precisionStep
argument_list|)
operator|.
name|append
argument_list|(
literal|')'
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
comment|// members
DECL|field|termAtt
specifier|private
specifier|final
name|TermAttribute
name|termAtt
decl_stmt|;
DECL|field|typeAtt
specifier|private
specifier|final
name|TypeAttribute
name|typeAtt
decl_stmt|;
DECL|field|posIncrAtt
specifier|private
specifier|final
name|PositionIncrementAttribute
name|posIncrAtt
decl_stmt|;
DECL|field|shift
DECL|field|valSize
specifier|private
name|int
name|shift
init|=
literal|0
decl_stmt|,
name|valSize
init|=
literal|0
decl_stmt|;
comment|// valSize==0 means not initialized
DECL|field|precisionStep
specifier|private
specifier|final
name|int
name|precisionStep
decl_stmt|;
DECL|field|value
specifier|private
name|long
name|value
init|=
literal|0L
decl_stmt|;
block|}
end_class

end_unit

