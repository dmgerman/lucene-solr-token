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
name|util
operator|.
name|GeoUtils
import|;
end_import

begin_comment
comment|/**  *<p>  * Field that indexes<code>latitude</code><code>longitude</code> decimal-degree values  * for efficient encoding, sorting, and querying. This Geo capability is intended  * to provide a basic and efficient out of the box field type for indexing and  * querying 2 dimensional points in WGS-84 decimal degrees. An example usage is as follows:  *  *<pre class="prettyprint">  *  document.add(new GeoPointField(name, -96.33, 32.66, Field.Store.NO));  *</pre>  *  *<p>To perform simple geospatial queries against a<code>GeoPointField</code>,  * see {@link org.apache.lucene.search.GeoPointInBBoxQuery} or {@link org.apache.lucene.search.GeoPointInPolygonQuery}  *  * NOTE: This indexes only high precision encoded terms which may result in visiting a high number  * of terms for large queries. See LUCENE-6481 for a future improvement.  *  * @lucene.experimental  */
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
DECL|field|PRECISION_STEP
specifier|public
specifier|static
specifier|final
name|int
name|PRECISION_STEP
init|=
literal|6
decl_stmt|;
comment|/**    * Type for an GeoPointField that is not stored:    * normalization factors, frequencies, and positions are omitted.    */
DECL|field|TYPE_NOT_STORED
specifier|public
specifier|static
specifier|final
name|FieldType
name|TYPE_NOT_STORED
init|=
operator|new
name|FieldType
argument_list|()
decl_stmt|;
static|static
block|{
name|TYPE_NOT_STORED
operator|.
name|setTokenized
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|TYPE_NOT_STORED
operator|.
name|setOmitNorms
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|TYPE_NOT_STORED
operator|.
name|setIndexOptions
argument_list|(
name|IndexOptions
operator|.
name|DOCS
argument_list|)
expr_stmt|;
name|TYPE_NOT_STORED
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
name|TYPE_NOT_STORED
operator|.
name|setNumericPrecisionStep
argument_list|(
name|PRECISION_STEP
argument_list|)
expr_stmt|;
name|TYPE_NOT_STORED
operator|.
name|freeze
argument_list|()
expr_stmt|;
block|}
comment|/**    * Type for a stored GeoPointField:    * normalization factors, frequencies, and positions are omitted.    */
DECL|field|TYPE_STORED
specifier|public
specifier|static
specifier|final
name|FieldType
name|TYPE_STORED
init|=
operator|new
name|FieldType
argument_list|()
decl_stmt|;
static|static
block|{
name|TYPE_STORED
operator|.
name|setTokenized
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|TYPE_STORED
operator|.
name|setOmitNorms
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|TYPE_STORED
operator|.
name|setIndexOptions
argument_list|(
name|IndexOptions
operator|.
name|DOCS
argument_list|)
expr_stmt|;
name|TYPE_STORED
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
name|TYPE_STORED
operator|.
name|setNumericPrecisionStep
argument_list|(
name|PRECISION_STEP
argument_list|)
expr_stmt|;
name|TYPE_STORED
operator|.
name|setStored
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|TYPE_STORED
operator|.
name|freeze
argument_list|()
expr_stmt|;
block|}
comment|/** Creates a stored or un-stored GeoPointField with the provided value    *  and default<code>precisionStep</code> set to 64 to avoid wasteful    *  indexing of lower precision terms.    *  @param name field name    *  @param lon longitude double value [-180.0 : 180.0]    *  @param lat latitude double value [-90.0 : 90.0]    *  @param stored Store.YES if the content should also be stored    *  @throws IllegalArgumentException if the field name is null.    */
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
name|super
argument_list|(
name|name
argument_list|,
name|stored
operator|==
name|Store
operator|.
name|YES
condition|?
name|TYPE_STORED
else|:
name|TYPE_NOT_STORED
argument_list|)
expr_stmt|;
name|fieldsData
operator|=
name|GeoUtils
operator|.
name|mortonHash
argument_list|(
name|lon
argument_list|,
name|lat
argument_list|)
expr_stmt|;
block|}
comment|/** Expert: allows you to customize the {@link    *  FieldType}.    *  @param name field name    *  @param lon longitude double value [-180.0 : 180.0]    *  @param lat latitude double value [-90.0 : 90.0]    *  @param type customized field type: must have {@link FieldType#numericType()}    *         of {@link FieldType.NumericType#LONG}.    *  @throws IllegalArgumentException if the field name or type is null, or    *          if the field type does not have a LONG numericType()    */
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
name|GeoUtils
operator|.
name|mortonHash
argument_list|(
name|lon
argument_list|,
name|lat
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit
