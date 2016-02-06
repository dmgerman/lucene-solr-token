begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.spatial.document
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|document
package|;
end_package

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
name|Field
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
name|FieldType
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
name|DocValuesType
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
name|analysis
operator|.
name|Analyzer
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
name|spatial
operator|.
name|util
operator|.
name|GeoEncodingUtils
import|;
end_import

begin_comment
comment|/**  *<p>  * Field that indexes<code>latitude</code><code>longitude</code> decimal-degree values  * for efficient encoding, sorting, and querying. This Geo capability is intended  * to provide a basic and efficient out of the box field type for indexing and  * querying 2 dimensional points in WGS-84 decimal degrees. An example usage is as follows:  *  *<pre class="prettyprint">  *  document.add(new GeoPointField(name, -96.33, 32.66, Field.Store.NO));  *</pre>  *  *<p>To perform simple geospatial queries against a<code>GeoPointField</code>,  * see {@link org.apache.lucene.spatial.search.GeoPointInBBoxQuery}, {@link org.apache.lucene.spatial.search.GeoPointInPolygonQuery},  * or {@link org.apache.lucene.spatial.search.GeoPointDistanceQuery}  *  * NOTE: This indexes only high precision encoded terms which may result in visiting a high number  * of terms for large queries. See LUCENE-6481 for a future improvement.  *  * @lucene.experimental  */
end_comment

begin_class
DECL|class|GeoPointField
specifier|public
specifier|final
class|class
name|GeoPointField
extends|extends
name|Field
block|{
comment|/** encoding step value for GeoPoint prefix terms */
DECL|field|PRECISION_STEP
specifier|public
specifier|static
specifier|final
name|int
name|PRECISION_STEP
init|=
literal|9
decl_stmt|;
comment|/**    *<b>Expert:</b> Optional flag to select term encoding for GeoPointField types    */
DECL|enum|TermEncoding
specifier|public
enum|enum
name|TermEncoding
block|{
comment|/**      * encodes prefix terms only resulting in a small index and faster queries - use with      * {@code GeoPointTokenStream}      */
DECL|enum constant|PREFIX
name|PREFIX
block|,
comment|/**      * @deprecated encodes prefix and full resolution terms - use with      * {@link org.apache.lucene.analysis.LegacyNumericTokenStream}      */
DECL|enum constant|Deprecated
annotation|@
name|Deprecated
DECL|enum constant|NUMERIC
name|NUMERIC
block|}
comment|/**    * @deprecated Type for a GeoPointField that is not stored:    * normalization factors, frequencies, and positions are omitted.    */
annotation|@
name|Deprecated
DECL|field|NUMERIC_TYPE_NOT_STORED
specifier|public
specifier|static
specifier|final
name|FieldType
name|NUMERIC_TYPE_NOT_STORED
init|=
operator|new
name|FieldType
argument_list|()
decl_stmt|;
static|static
block|{
name|NUMERIC_TYPE_NOT_STORED
operator|.
name|setTokenized
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|NUMERIC_TYPE_NOT_STORED
operator|.
name|setOmitNorms
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|NUMERIC_TYPE_NOT_STORED
operator|.
name|setIndexOptions
argument_list|(
name|IndexOptions
operator|.
name|DOCS
argument_list|)
expr_stmt|;
name|NUMERIC_TYPE_NOT_STORED
operator|.
name|setDocValuesType
argument_list|(
name|DocValuesType
operator|.
name|SORTED_NUMERIC
argument_list|)
expr_stmt|;
name|NUMERIC_TYPE_NOT_STORED
operator|.
name|setNumericType
argument_list|(
name|FieldType
operator|.
name|LegacyNumericType
operator|.
name|LONG
argument_list|)
expr_stmt|;
name|NUMERIC_TYPE_NOT_STORED
operator|.
name|setNumericPrecisionStep
argument_list|(
name|PRECISION_STEP
argument_list|)
expr_stmt|;
name|NUMERIC_TYPE_NOT_STORED
operator|.
name|freeze
argument_list|()
expr_stmt|;
block|}
comment|/**    * @deprecated Type for a stored GeoPointField:    * normalization factors, frequencies, and positions are omitted.    */
annotation|@
name|Deprecated
DECL|field|NUMERIC_TYPE_STORED
specifier|public
specifier|static
specifier|final
name|FieldType
name|NUMERIC_TYPE_STORED
init|=
operator|new
name|FieldType
argument_list|()
decl_stmt|;
static|static
block|{
name|NUMERIC_TYPE_STORED
operator|.
name|setTokenized
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|NUMERIC_TYPE_STORED
operator|.
name|setOmitNorms
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|NUMERIC_TYPE_STORED
operator|.
name|setIndexOptions
argument_list|(
name|IndexOptions
operator|.
name|DOCS
argument_list|)
expr_stmt|;
name|NUMERIC_TYPE_STORED
operator|.
name|setDocValuesType
argument_list|(
name|DocValuesType
operator|.
name|SORTED_NUMERIC
argument_list|)
expr_stmt|;
name|NUMERIC_TYPE_STORED
operator|.
name|setNumericType
argument_list|(
name|FieldType
operator|.
name|LegacyNumericType
operator|.
name|LONG
argument_list|)
expr_stmt|;
name|NUMERIC_TYPE_STORED
operator|.
name|setNumericPrecisionStep
argument_list|(
name|PRECISION_STEP
argument_list|)
expr_stmt|;
name|NUMERIC_TYPE_STORED
operator|.
name|setStored
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|NUMERIC_TYPE_STORED
operator|.
name|freeze
argument_list|()
expr_stmt|;
block|}
comment|/**    * Type for a GeoPointField that is not stored:    * normalization factors, frequencies, and positions are omitted.    */
DECL|field|PREFIX_TYPE_NOT_STORED
specifier|public
specifier|static
specifier|final
name|FieldType
name|PREFIX_TYPE_NOT_STORED
init|=
operator|new
name|FieldType
argument_list|()
decl_stmt|;
static|static
block|{
name|PREFIX_TYPE_NOT_STORED
operator|.
name|setTokenized
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|PREFIX_TYPE_NOT_STORED
operator|.
name|setOmitNorms
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|PREFIX_TYPE_NOT_STORED
operator|.
name|setIndexOptions
argument_list|(
name|IndexOptions
operator|.
name|DOCS
argument_list|)
expr_stmt|;
name|PREFIX_TYPE_NOT_STORED
operator|.
name|setDocValuesType
argument_list|(
name|DocValuesType
operator|.
name|SORTED_NUMERIC
argument_list|)
expr_stmt|;
name|PREFIX_TYPE_NOT_STORED
operator|.
name|freeze
argument_list|()
expr_stmt|;
block|}
comment|/**    * Type for a stored GeoPointField:    * normalization factors, frequencies, and positions are omitted.    */
DECL|field|PREFIX_TYPE_STORED
specifier|public
specifier|static
specifier|final
name|FieldType
name|PREFIX_TYPE_STORED
init|=
operator|new
name|FieldType
argument_list|()
decl_stmt|;
static|static
block|{
name|PREFIX_TYPE_STORED
operator|.
name|setTokenized
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|PREFIX_TYPE_STORED
operator|.
name|setOmitNorms
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|PREFIX_TYPE_STORED
operator|.
name|setIndexOptions
argument_list|(
name|IndexOptions
operator|.
name|DOCS
argument_list|)
expr_stmt|;
name|PREFIX_TYPE_STORED
operator|.
name|setDocValuesType
argument_list|(
name|DocValuesType
operator|.
name|SORTED_NUMERIC
argument_list|)
expr_stmt|;
name|PREFIX_TYPE_STORED
operator|.
name|setStored
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|PREFIX_TYPE_STORED
operator|.
name|freeze
argument_list|()
expr_stmt|;
block|}
comment|/** Creates a stored or un-stored GeoPointField    *  @param name field name    *  @param lon longitude double value [-180.0 : 180.0]    *  @param lat latitude double value [-90.0 : 90.0]    *  @param stored Store.YES if the content should also be stored    *  @throws IllegalArgumentException if the field name is null.    */
DECL|method|GeoPointField
specifier|public
name|GeoPointField
parameter_list|(
name|String
name|name
parameter_list|,
name|double
name|lon
parameter_list|,
name|double
name|lat
parameter_list|,
name|Store
name|stored
parameter_list|)
block|{
name|this
argument_list|(
name|name
argument_list|,
name|lon
argument_list|,
name|lat
argument_list|,
name|getFieldType
argument_list|(
name|stored
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Creates a stored or un-stored GeoPointField using the specified {@link TermEncoding} method    *  @param name field name    *  @param lon longitude double value [-180.0 : 180.0]    *  @param lat latitude double value [-90.0 : 90.0]    *  @param termEncoding encoding type to use ({@link TermEncoding#NUMERIC} Terms, or {@link TermEncoding#PREFIX} only Terms)    *  @param stored Store.YES if the content should also be stored    *  @throws IllegalArgumentException if the field name is null.    */
annotation|@
name|Deprecated
DECL|method|GeoPointField
specifier|public
name|GeoPointField
parameter_list|(
name|String
name|name
parameter_list|,
name|double
name|lon
parameter_list|,
name|double
name|lat
parameter_list|,
name|TermEncoding
name|termEncoding
parameter_list|,
name|Store
name|stored
parameter_list|)
block|{
name|this
argument_list|(
name|name
argument_list|,
name|lon
argument_list|,
name|lat
argument_list|,
name|getFieldType
argument_list|(
name|termEncoding
argument_list|,
name|stored
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Expert: allows you to customize the {@link    *  FieldType}.    *  @param name field name    *  @param lon longitude double value [-180.0 : 180.0]    *  @param lat latitude double value [-90.0 : 90.0]    *  @param type customized field type: must have {@link FieldType#numericType()}    *         of {@link org.apache.lucene.document.FieldType.LegacyNumericType#LONG}.    *  @throws IllegalArgumentException if the field name or type is null, or    *          if the field type does not have a LONG numericType()    */
DECL|method|GeoPointField
specifier|public
name|GeoPointField
parameter_list|(
name|String
name|name
parameter_list|,
name|double
name|lon
parameter_list|,
name|double
name|lat
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
comment|// field must be indexed
comment|// todo does it make sense here to provide the ability to store a GeoPointField but not index?
if|if
condition|(
name|type
operator|.
name|indexOptions
argument_list|()
operator|==
name|IndexOptions
operator|.
name|NONE
operator|&&
name|type
operator|.
name|stored
argument_list|()
operator|==
literal|false
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"type.indexOptions() is set to NONE but type.stored() is false"
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|type
operator|.
name|indexOptions
argument_list|()
operator|==
name|IndexOptions
operator|.
name|DOCS
condition|)
block|{
if|if
condition|(
name|type
operator|.
name|docValuesType
argument_list|()
operator|!=
name|DocValuesType
operator|.
name|SORTED_NUMERIC
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"type.docValuesType() must be SORTED_NUMERIC but got "
operator|+
name|type
operator|.
name|docValuesType
argument_list|()
argument_list|)
throw|;
block|}
if|if
condition|(
name|type
operator|.
name|numericType
argument_list|()
operator|!=
literal|null
condition|)
block|{
comment|// make sure numericType is a LONG
if|if
condition|(
name|type
operator|.
name|numericType
argument_list|()
operator|!=
name|FieldType
operator|.
name|LegacyNumericType
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
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"type.indexOptions() must be one of NONE or DOCS but got "
operator|+
name|type
operator|.
name|indexOptions
argument_list|()
argument_list|)
throw|;
block|}
comment|// set field data
name|fieldsData
operator|=
name|GeoEncodingUtils
operator|.
name|mortonHash
argument_list|(
name|lon
argument_list|,
name|lat
argument_list|)
expr_stmt|;
block|}
DECL|method|getFieldType
specifier|private
specifier|static
name|FieldType
name|getFieldType
parameter_list|(
name|Store
name|stored
parameter_list|)
block|{
return|return
name|getFieldType
argument_list|(
name|TermEncoding
operator|.
name|PREFIX
argument_list|,
name|stored
argument_list|)
return|;
block|}
comment|/**    * @deprecated    * Static helper method for returning a valid FieldType based on termEncoding and stored options    */
annotation|@
name|Deprecated
DECL|method|getFieldType
specifier|private
specifier|static
name|FieldType
name|getFieldType
parameter_list|(
name|TermEncoding
name|termEncoding
parameter_list|,
name|Store
name|stored
parameter_list|)
block|{
if|if
condition|(
name|stored
operator|==
name|Store
operator|.
name|YES
condition|)
block|{
return|return
name|termEncoding
operator|==
name|TermEncoding
operator|.
name|PREFIX
condition|?
name|PREFIX_TYPE_STORED
else|:
name|NUMERIC_TYPE_STORED
return|;
block|}
elseif|else
if|if
condition|(
name|stored
operator|==
name|Store
operator|.
name|NO
condition|)
block|{
return|return
name|termEncoding
operator|==
name|TermEncoding
operator|.
name|PREFIX
condition|?
name|PREFIX_TYPE_NOT_STORED
else|:
name|NUMERIC_TYPE_NOT_STORED
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"stored option must be NO or YES but got "
operator|+
name|stored
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|tokenStream
specifier|public
name|TokenStream
name|tokenStream
parameter_list|(
name|Analyzer
name|analyzer
parameter_list|,
name|TokenStream
name|reuse
parameter_list|)
block|{
if|if
condition|(
name|fieldType
argument_list|()
operator|.
name|indexOptions
argument_list|()
operator|==
name|IndexOptions
operator|.
name|NONE
condition|)
block|{
comment|// not indexed
return|return
literal|null
return|;
block|}
comment|// if numericType is set
if|if
condition|(
name|type
operator|.
name|numericType
argument_list|()
operator|!=
literal|null
condition|)
block|{
comment|// return numeric encoding
return|return
name|super
operator|.
name|tokenStream
argument_list|(
name|analyzer
argument_list|,
name|reuse
argument_list|)
return|;
block|}
if|if
condition|(
name|reuse
operator|instanceof
name|GeoPointTokenStream
operator|==
literal|false
condition|)
block|{
name|reuse
operator|=
operator|new
name|GeoPointTokenStream
argument_list|()
expr_stmt|;
block|}
specifier|final
name|GeoPointTokenStream
name|gpts
init|=
operator|(
name|GeoPointTokenStream
operator|)
name|reuse
decl_stmt|;
name|gpts
operator|.
name|setGeoCode
argument_list|(
operator|(
operator|(
name|Number
operator|)
name|fieldsData
operator|)
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|reuse
return|;
block|}
comment|/** access longitude value */
DECL|method|getLon
specifier|public
name|double
name|getLon
parameter_list|()
block|{
return|return
name|GeoEncodingUtils
operator|.
name|mortonUnhashLon
argument_list|(
operator|(
name|long
operator|)
name|fieldsData
argument_list|)
return|;
block|}
comment|/** access latitude value */
DECL|method|getLat
specifier|public
name|double
name|getLat
parameter_list|()
block|{
return|return
name|GeoEncodingUtils
operator|.
name|mortonUnhashLat
argument_list|(
operator|(
name|long
operator|)
name|fieldsData
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
if|if
condition|(
name|fieldsData
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|GeoEncodingUtils
operator|.
name|mortonUnhashLon
argument_list|(
operator|(
name|long
operator|)
name|fieldsData
argument_list|)
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|','
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|GeoEncodingUtils
operator|.
name|mortonUnhashLat
argument_list|(
operator|(
name|long
operator|)
name|fieldsData
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

