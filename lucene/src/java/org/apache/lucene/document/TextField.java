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
name|TokenStream
import|;
end_import

begin_comment
comment|/** A field that is indexed and tokenized, without term  *  vectors.  For example this would be used on a 'body'  *  field, that contains the bulk of a document's text.  *   *  This field's value is not stored by default; use the  *  {@link TextField#TYPE_STORED} type (pass it to<code>new  *  Field</code>) to store the value. */
end_comment

begin_class
DECL|class|TextField
specifier|public
specifier|final
class|class
name|TextField
extends|extends
name|Field
block|{
comment|/* Indexed, tokenized, not stored. */
DECL|field|TYPE_UNSTORED
specifier|public
specifier|static
specifier|final
name|FieldType
name|TYPE_UNSTORED
init|=
operator|new
name|FieldType
argument_list|()
decl_stmt|;
comment|/* Indexed, tokenized, stored. */
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
name|TYPE_UNSTORED
operator|.
name|setIndexed
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|TYPE_UNSTORED
operator|.
name|setTokenized
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|TYPE_UNSTORED
operator|.
name|freeze
argument_list|()
expr_stmt|;
name|TYPE_STORED
operator|.
name|setIndexed
argument_list|(
literal|true
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
name|setTokenized
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
comment|/** Creates a new un-stored TextField */
DECL|method|TextField
specifier|public
name|TextField
parameter_list|(
name|String
name|name
parameter_list|,
name|Reader
name|reader
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|TextField
operator|.
name|TYPE_UNSTORED
argument_list|,
name|reader
argument_list|)
expr_stmt|;
block|}
comment|/** Creates a new un-stored TextField */
DECL|method|TextField
specifier|public
name|TextField
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|value
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|TextField
operator|.
name|TYPE_UNSTORED
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
comment|/** Creates a new un-stored TextField */
DECL|method|TextField
specifier|public
name|TextField
parameter_list|(
name|String
name|name
parameter_list|,
name|TokenStream
name|stream
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|TextField
operator|.
name|TYPE_UNSTORED
argument_list|,
name|stream
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

