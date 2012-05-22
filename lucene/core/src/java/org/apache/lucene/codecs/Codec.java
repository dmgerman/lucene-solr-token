begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.codecs
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
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
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ServiceLoader
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
name|IndexFileNames
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
name|IndexWriterConfig
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
name|SegmentInfo
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
name|NamedSPILoader
import|;
end_import

begin_comment
comment|/**  * Encodes/decodes an inverted index segment.  *<p>  * Note, when extending this class, the name ({@link #getName}) is   * written into the index. In order for the segment to be read, the  * name must resolve to your implementation via {@link #forName(String)}.  * This method uses Java's   * {@link ServiceLoader Service Provider Interface} to resolve codec names.  *<p>  * @see ServiceLoader  */
end_comment

begin_class
DECL|class|Codec
specifier|public
specifier|abstract
class|class
name|Codec
implements|implements
name|NamedSPILoader
operator|.
name|NamedSPI
block|{
DECL|field|loader
specifier|private
specifier|static
specifier|final
name|NamedSPILoader
argument_list|<
name|Codec
argument_list|>
name|loader
init|=
operator|new
name|NamedSPILoader
argument_list|<
name|Codec
argument_list|>
argument_list|(
name|Codec
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|name
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
DECL|method|Codec
specifier|public
name|Codec
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|NamedSPILoader
operator|.
name|checkServiceName
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
comment|/** Returns this codec's name */
annotation|@
name|Override
DECL|method|getName
specifier|public
specifier|final
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
comment|/** Encodes/decodes postings */
DECL|method|postingsFormat
specifier|public
specifier|abstract
name|PostingsFormat
name|postingsFormat
parameter_list|()
function_decl|;
comment|/** Encodes/decodes docvalues */
DECL|method|docValuesFormat
specifier|public
specifier|abstract
name|DocValuesFormat
name|docValuesFormat
parameter_list|()
function_decl|;
comment|/** Encodes/decodes stored fields */
DECL|method|storedFieldsFormat
specifier|public
specifier|abstract
name|StoredFieldsFormat
name|storedFieldsFormat
parameter_list|()
function_decl|;
comment|/** Encodes/decodes term vectors */
DECL|method|termVectorsFormat
specifier|public
specifier|abstract
name|TermVectorsFormat
name|termVectorsFormat
parameter_list|()
function_decl|;
comment|/** Encodes/decodes field infos file */
DECL|method|fieldInfosFormat
specifier|public
specifier|abstract
name|FieldInfosFormat
name|fieldInfosFormat
parameter_list|()
function_decl|;
comment|/** Encodes/decodes segments file */
DECL|method|segmentInfosFormat
specifier|public
specifier|abstract
name|SegmentInfoFormat
name|segmentInfosFormat
parameter_list|()
function_decl|;
comment|/** Encodes/decodes document normalization values */
DECL|method|normsFormat
specifier|public
specifier|abstract
name|NormsFormat
name|normsFormat
parameter_list|()
function_decl|;
comment|/** Encodes/decodes live docs */
DECL|method|liveDocsFormat
specifier|public
specifier|abstract
name|LiveDocsFormat
name|liveDocsFormat
parameter_list|()
function_decl|;
comment|/** looks up a codec by name */
DECL|method|forName
specifier|public
specifier|static
name|Codec
name|forName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|loader
operator|.
name|lookup
argument_list|(
name|name
argument_list|)
return|;
block|}
comment|/** returns a list of all available codec names */
DECL|method|availableCodecs
specifier|public
specifier|static
name|Set
argument_list|<
name|String
argument_list|>
name|availableCodecs
parameter_list|()
block|{
return|return
name|loader
operator|.
name|availableServices
argument_list|()
return|;
block|}
DECL|field|defaultCodec
specifier|private
specifier|static
name|Codec
name|defaultCodec
init|=
name|Codec
operator|.
name|forName
argument_list|(
literal|"Lucene40"
argument_list|)
decl_stmt|;
comment|/** expert: returns the default codec used for newly created    *  {@link IndexWriterConfig}s.    */
comment|// TODO: should we use this, or maybe a system property is better?
DECL|method|getDefault
specifier|public
specifier|static
name|Codec
name|getDefault
parameter_list|()
block|{
return|return
name|defaultCodec
return|;
block|}
comment|/** expert: sets the default codec used for newly created    *  {@link IndexWriterConfig}s.    */
DECL|method|setDefault
specifier|public
specifier|static
name|void
name|setDefault
parameter_list|(
name|Codec
name|codec
parameter_list|)
block|{
name|defaultCodec
operator|=
name|codec
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|name
return|;
block|}
block|}
end_class

end_unit

