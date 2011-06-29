begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.facet.taxonomy.lucene
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|taxonomy
operator|.
name|lucene
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
name|FieldSelector
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
name|FieldSelectorResult
import|;
end_import

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * @lucene.experimental  */
end_comment

begin_class
DECL|class|Consts
specifier|abstract
class|class
name|Consts
block|{
DECL|field|FULL
specifier|static
specifier|final
name|String
name|FULL
init|=
literal|"$full_path$"
decl_stmt|;
DECL|field|FIELD_PAYLOADS
specifier|static
specifier|final
name|String
name|FIELD_PAYLOADS
init|=
literal|"$payloads$"
decl_stmt|;
DECL|field|PAYLOAD_PARENT
specifier|static
specifier|final
name|String
name|PAYLOAD_PARENT
init|=
literal|"p"
decl_stmt|;
DECL|field|PAYLOAD_PARENT_CHARS
specifier|static
specifier|final
name|char
index|[]
name|PAYLOAD_PARENT_CHARS
init|=
name|PAYLOAD_PARENT
operator|.
name|toCharArray
argument_list|()
decl_stmt|;
comment|/**    * The following is a "field selector", an object which tells Lucene to    * extract only a single field rather than a whole document.    */
DECL|field|fullPathSelector
specifier|public
specifier|static
specifier|final
name|FieldSelector
name|fullPathSelector
init|=
operator|new
name|FieldSelector
argument_list|()
block|{
specifier|public
name|FieldSelectorResult
name|accept
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
if|if
condition|(
name|fieldName
operator|.
name|equals
argument_list|(
name|FULL
argument_list|)
condition|)
block|{
return|return
name|FieldSelectorResult
operator|.
name|LOAD_AND_BREAK
return|;
block|}
return|return
name|FieldSelectorResult
operator|.
name|NO_LOAD
return|;
block|}
block|}
decl_stmt|;
comment|/**    * Delimiter used for creating the full path of a category from the list of    * its labels from root. It is forbidden for labels to contain this    * character.    *<P>    * Originally, we used \uFFFE, officially a "unicode noncharacter" (invalid    * unicode character) for this purpose. Recently, we switched to the    * "private-use" character \uF749.    */
comment|//static final char DEFAULT_DELIMITER = '\uFFFE';
DECL|field|DEFAULT_DELIMITER
specifier|static
specifier|final
name|char
name|DEFAULT_DELIMITER
init|=
literal|'\uF749'
decl_stmt|;
block|}
end_class

end_unit

