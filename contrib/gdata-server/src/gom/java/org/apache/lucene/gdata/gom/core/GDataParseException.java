begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.gdata.gom.core
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|gdata
operator|.
name|gom
operator|.
name|core
package|;
end_package

begin_comment
comment|/**  * @author Simon Willnauer  *   */
end_comment

begin_class
DECL|class|GDataParseException
specifier|public
class|class
name|GDataParseException
extends|extends
name|RuntimeException
block|{
comment|/** 	 * Serial Version ID -- implements {@link java.io.Serializable} 	 */
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
operator|-
literal|3633604155009277238L
decl_stmt|;
comment|/** 	 *  	 */
DECL|method|GDataParseException
specifier|public
name|GDataParseException
parameter_list|()
block|{ 	}
comment|/** 	 * @param arg0 	 */
DECL|method|GDataParseException
specifier|public
name|GDataParseException
parameter_list|(
name|String
name|arg0
parameter_list|)
block|{
name|super
argument_list|(
name|arg0
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * @param arg0 	 */
DECL|method|GDataParseException
specifier|public
name|GDataParseException
parameter_list|(
name|Throwable
name|arg0
parameter_list|)
block|{
name|super
argument_list|(
name|arg0
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * @param arg0 	 * @param arg1 	 */
DECL|method|GDataParseException
specifier|public
name|GDataParseException
parameter_list|(
name|String
name|arg0
parameter_list|,
name|Throwable
name|arg1
parameter_list|)
block|{
name|super
argument_list|(
name|arg0
argument_list|,
name|arg1
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

