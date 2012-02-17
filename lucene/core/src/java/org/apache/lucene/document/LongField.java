begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.document
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
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
name|analysis
operator|.
name|NumericTokenStream
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
name|index
operator|.
name|FieldInfo
operator|.
name|IndexOptions
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
name|search
operator|.
name|NumericRangeFilter
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
name|search
operator|.
name|NumericRangeQuery
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
name|util
operator|.
name|NumericUtils
import|;
end_import

begin_comment
comment|/**  *<p>  * This class provides a {@link Field} that enables indexing of long values  * for efficient range filtering and sorting. Here's an example usage:  *   *<pre>  * document.add(new LongField(name, 6L));  *</pre>  *   * For optimal performance, re-use the<code>LongField</code> and  * {@link Document} instance for more than one document:  *   *<pre>  *  LongField field = new LongField(name, 0L);  *  Document document = new Document();  *  document.add(field);  *   *  for(all documents) {  *    ...  *    field.setLongValue(value)  *    writer.addDocument(document);  *    ...  *  }  *</pre>  *  * See also {@link IntField}, {@link FloatField}, {@link  * DoubleField}.  *  * Any type that can be converted to long can also be  * indexed.  For example, date/time values represented by a  * {@link java.util.Date} can be translated into a long  * value using the {@link java.util.Date#getTime} method.  If you  * don't need millisecond precision, you can quantize the  * value, either by dividing the result of  * {@link java.util.Date#getTime} or using the separate getters  * (for year, month, etc.) to construct an<code>int</code> or  *<code>long</code> value.</p>  *  *<p>To perform range querying or filtering against a  *<code>LongField</code>, use {@link NumericRangeQuery} or {@link  * NumericRangeFilter}.  To sort according to a  *<code>LongField</code>, use the normal numeric sort types, eg  * {@link org.apache.lucene.search.SortField.Type#LONG}.<code>LongField</code>   * values can also be loaded directly from {@link FieldCache}.</p>  *  *<p>By default, a<code>LongField</code>'s value is not stored but  * is indexed for range filtering and sorting.  You can use  * {@link StoredField} to also store the value.  *  *<p>You may add the same field name as an<code>LongField</code> to  * the same document more than once.  Range querying and  * filtering will be the logical OR of all values; so a range query  * will hit all documents that have at least one value in  * the range. However sort behavior is not defined.  If you need to sort,  * you should separately index a single-valued<code>LongField</code>.</p>  *  *<p>A<code>LongField</code> will consume somewhat more disk space  * in the index than an ordinary single-valued field.  * However, for a typical index that includes substantial  * textual content per document, this increase will likely  * be in the noise.</p>  *  *<p>Within Lucene, each numeric value is indexed as a  *<em>trie</em> structure, where each term is logically  * assigned to larger and larger pre-defined brackets (which  * are simply lower-precision representations of the value).  * The step size between each successive bracket is called the  *<code>precisionStep</code>, measured in bits.  Smaller  *<code>precisionStep</code> values result in larger number  * of brackets, which consumes more disk space in the index  * but may result in faster range search performance.  The  * default value, 4, was selected for a reasonable tradeoff  * of disk space consumption versus performance.  You can  * create a custom {@link FieldType} and invoke the {@link  * FieldType#setNumericPrecisionStep} method if you'd  * like to change the value.  Note that you must also  * specify a congruent value when creating {@link  * NumericRangeQuery} or {@link NumericRangeFilter}.  * For low cardinality fields larger precision steps are good.  * If the cardinality is&lt; 100, it is fair  * to use {@link Integer#MAX_VALUE}, which produces one  * term per value.  *  *<p>For more information on the internals of numeric trie  * indexing, including the<a  * href="../search/NumericRangeQuery.html#precisionStepDesc"><code>precisionStep</code></a>  * configuration, see {@link NumericRangeQuery}. The format of  * indexed values is described in {@link NumericUtils}.  *  *<p>If you only need to sort by numeric value, and never  * run range querying/filtering, you can index using a  *<code>precisionStep</code> of {@link Integer#MAX_VALUE}.  * This will minimize disk space consumed.</p>  *  *<p>More advanced users can instead use {@link  * NumericTokenStream} directly, when indexing numbers. This  * class is a wrapper around this token stream type for  * easier, more intuitive usage.</p>  *  * @since 2.9  */
end_comment

begin_class
DECL|class|LongField
specifier|public
specifier|final
class|class
name|LongField
extends|extends
name|Field
block|{
DECL|field|TYPE
specifier|public
specifier|static
specifier|final
name|FieldType
name|TYPE
init|=
operator|new
name|FieldType
argument_list|()
decl_stmt|;
static|static
block|{
name|TYPE
operator|.
name|setIndexed
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|TYPE
operator|.
name|setTokenized
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|TYPE
operator|.
name|setOmitNorms
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|TYPE
operator|.
name|setIndexOptions
argument_list|(
name|IndexOptions
operator|.
name|DOCS_ONLY
argument_list|)
expr_stmt|;
name|TYPE
operator|.
name|setNumericType
argument_list|(
name|FieldType
operator|.
name|NumericType
operator|.
name|LONG
argument_list|)
expr_stmt|;
name|TYPE
operator|.
name|freeze
argument_list|()
expr_stmt|;
block|}
comment|/** Creates an LongField with the provided value    *  and default<code>precisionStep</code> {@link    *  NumericUtils#PRECISION_STEP_DEFAULT} (4). */
DECL|method|LongField
specifier|public
name|LongField
parameter_list|(
name|String
name|name
parameter_list|,
name|long
name|value
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|TYPE
argument_list|)
expr_stmt|;
name|fieldsData
operator|=
name|Long
operator|.
name|valueOf
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
comment|/** Expert: allows you to customize the {@link    *  FieldType}. */
DECL|method|LongField
specifier|public
name|LongField
parameter_list|(
name|String
name|name
parameter_list|,
name|long
name|value
parameter_list|,
name|FieldType
name|type
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|type
argument_list|)
expr_stmt|;
if|if
condition|(
name|type
operator|.
name|numericType
argument_list|()
operator|!=
name|FieldType
operator|.
name|NumericType
operator|.
name|LONG
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"type.numericType() must be LONG but got "
operator|+
name|type
operator|.
name|numericType
argument_list|()
argument_list|)
throw|;
block|}
name|fieldsData
operator|=
name|Long
operator|.
name|valueOf
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

