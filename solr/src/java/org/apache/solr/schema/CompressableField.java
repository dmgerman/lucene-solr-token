begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.schema
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|schema
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
name|solr
operator|.
name|common
operator|.
name|params
operator|.
name|MapSolrParams
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|params
operator|.
name|SolrParams
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
comment|/**<code>CompressableField</code> is an abstract field type which enables a  * field to be compressed (by specifying<code>compressed="true"</code> at the  * field definition level) and provides optional support for specifying a  * threshold at which compression is enabled.  *  * Optional settings:  *<ul>  *<li><code>compressThreshold</code>: length, in characters, at which point the   *      field contents should be compressed [default: 0]</li>  *</ul></p>  *   * TODO: Enable compression level specification (not yet in lucene)  *   * @version $Id$  */
end_comment

begin_class
DECL|class|CompressableField
specifier|public
specifier|abstract
class|class
name|CompressableField
extends|extends
name|FieldType
block|{
comment|/* if field size (in characters) is greater than this threshold, the field       will be stored compressed */
DECL|field|DEFAULT_COMPRESS_THRESHOLD
specifier|public
specifier|static
name|int
name|DEFAULT_COMPRESS_THRESHOLD
init|=
literal|0
decl_stmt|;
DECL|field|compressThreshold
name|int
name|compressThreshold
decl_stmt|;
DECL|field|CT
specifier|private
specifier|static
name|String
name|CT
init|=
literal|"compressThreshold"
decl_stmt|;
DECL|method|init
specifier|protected
name|void
name|init
parameter_list|(
name|IndexSchema
name|schema
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
parameter_list|)
block|{
name|SolrParams
name|p
init|=
operator|new
name|MapSolrParams
argument_list|(
name|args
argument_list|)
decl_stmt|;
name|compressThreshold
operator|=
name|p
operator|.
name|getInt
argument_list|(
name|CT
argument_list|,
name|DEFAULT_COMPRESS_THRESHOLD
argument_list|)
expr_stmt|;
name|args
operator|.
name|remove
argument_list|(
name|CT
argument_list|)
expr_stmt|;
name|super
operator|.
name|init
argument_list|(
name|schema
argument_list|,
name|args
argument_list|)
expr_stmt|;
block|}
comment|/* Helpers for field construction */
DECL|method|getFieldStore
specifier|protected
name|Field
operator|.
name|Store
name|getFieldStore
parameter_list|(
name|SchemaField
name|field
parameter_list|,
name|String
name|internalVal
parameter_list|)
block|{
comment|/* compress field if length exceeds threshold */
if|if
condition|(
name|field
operator|.
name|isCompressed
argument_list|()
condition|)
block|{
return|return
name|internalVal
operator|.
name|length
argument_list|()
operator|>=
name|compressThreshold
condition|?
name|Field
operator|.
name|Store
operator|.
name|COMPRESS
else|:
name|Field
operator|.
name|Store
operator|.
name|YES
return|;
block|}
else|else
return|return
name|super
operator|.
name|getFieldStore
argument_list|(
name|field
argument_list|,
name|internalVal
argument_list|)
return|;
block|}
block|}
end_class

end_unit

