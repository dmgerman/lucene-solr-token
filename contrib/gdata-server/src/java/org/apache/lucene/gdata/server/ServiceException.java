begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**   * Copyright 2004 The Apache Software Foundation   *   * Licensed under the Apache License, Version 2.0 (the "License");   * you may not use this file except in compliance with the License.   * You may obtain a copy of the License at   *   *     http://www.apache.org/licenses/LICENSE-2.0   *   * Unless required by applicable law or agreed to in writing, software   * distributed under the License is distributed on an "AS IS" BASIS,   * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   * See the License for the specific language governing permissions and   * limitations under the License.   */
end_comment

begin_package
DECL|package|org.apache.lucene.gdata.server
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|gdata
operator|.
name|server
package|;
end_package

begin_comment
comment|/**  * The ServiceException is used to encapsulate all {@link java.lang.Exception}  * throw by underlaying layers of the  * {@link org.apache.lucene.gdata.server.Service} layer.  *   * @author Simon Willnauer  *   */
end_comment

begin_class
DECL|class|ServiceException
specifier|public
class|class
name|ServiceException
extends|extends
name|Exception
block|{
comment|/**      *       */
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
operator|-
literal|7099825107871876584L
decl_stmt|;
comment|/**      * Constructs a new ServiceException      */
DECL|method|ServiceException
specifier|public
name|ServiceException
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
comment|/**      * Constructs a new ServiceException      * @param arg0 - the exception message      */
DECL|method|ServiceException
specifier|public
name|ServiceException
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
comment|/**      * Constructs a new ServiceException      * @param arg0 - the exceptin message      * @param arg1 - the exception cause      */
DECL|method|ServiceException
specifier|public
name|ServiceException
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
comment|/**      * Constructs a new ServiceException      * @param arg0 - the exception cause      */
DECL|method|ServiceException
specifier|public
name|ServiceException
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
block|}
end_class

end_unit

