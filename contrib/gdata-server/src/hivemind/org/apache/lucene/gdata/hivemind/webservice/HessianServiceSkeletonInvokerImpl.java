begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.gdata.hivemind.webservice
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|gdata
operator|.
name|hivemind
operator|.
name|webservice
package|;
end_package

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
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStream
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletRequest
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletResponse
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
import|;
end_import

begin_import
import|import
name|com
operator|.
name|caucho
operator|.
name|hessian
operator|.
name|io
operator|.
name|AbstractHessianOutput
import|;
end_import

begin_import
import|import
name|com
operator|.
name|caucho
operator|.
name|hessian
operator|.
name|io
operator|.
name|Hessian2Input
import|;
end_import

begin_import
import|import
name|com
operator|.
name|caucho
operator|.
name|hessian
operator|.
name|io
operator|.
name|Hessian2Output
import|;
end_import

begin_import
import|import
name|com
operator|.
name|caucho
operator|.
name|hessian
operator|.
name|io
operator|.
name|HessianOutput
import|;
end_import

begin_import
import|import
name|com
operator|.
name|caucho
operator|.
name|hessian
operator|.
name|io
operator|.
name|SerializerFactory
import|;
end_import

begin_import
import|import
name|com
operator|.
name|caucho
operator|.
name|hessian
operator|.
name|server
operator|.
name|HessianSkeleton
import|;
end_import

begin_comment
comment|/**  * Wrapps the hessian skeleton invokation logic.  * This is based on the protocol description of the hessian protocol  * @author Simon Willnauer  *  */
end_comment

begin_class
DECL|class|HessianServiceSkeletonInvokerImpl
class|class
name|HessianServiceSkeletonInvokerImpl
implements|implements
name|HessianServiceSkeletonInvoker
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|HessianServiceSkeletonInvokerImpl
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|serializerFactory
specifier|private
name|SerializerFactory
name|serializerFactory
decl_stmt|;
DECL|field|skeleton
specifier|private
specifier|final
name|HessianSkeleton
name|skeleton
decl_stmt|;
comment|/**      * Creates a new HessianServiceSkeletonInvoker      * @param skeleton - The skeleton instance to invoke to process the request      *       */
DECL|method|HessianServiceSkeletonInvokerImpl
name|HessianServiceSkeletonInvokerImpl
parameter_list|(
specifier|final
name|HessianSkeleton
name|skeleton
parameter_list|)
block|{
name|this
operator|.
name|skeleton
operator|=
name|skeleton
expr_stmt|;
block|}
comment|/**      * @throws Throwable       * @see org.apache.lucene.gdata.hivemind.webservice.HessianServiceSkeletonInvoker#invoke(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)      */
DECL|method|invoke
specifier|public
name|void
name|invoke
parameter_list|(
name|HttpServletRequest
name|arg0
parameter_list|,
name|HttpServletResponse
name|arg1
parameter_list|)
throws|throws
name|Throwable
block|{
name|InputStream
name|inputStream
init|=
name|arg0
operator|.
name|getInputStream
argument_list|()
decl_stmt|;
name|OutputStream
name|outputStream
init|=
name|arg1
operator|.
name|getOutputStream
argument_list|()
decl_stmt|;
comment|/*          *This works only with hessian>= hessian-3.0.20!!          *but remember this is not a framework            */
name|Hessian2Input
name|hessianInput
init|=
operator|new
name|Hessian2Input
argument_list|(
name|inputStream
argument_list|)
decl_stmt|;
if|if
condition|(
name|this
operator|.
name|serializerFactory
operator|!=
literal|null
condition|)
block|{
name|hessianInput
operator|.
name|setSerializerFactory
argument_list|(
name|this
operator|.
name|serializerFactory
argument_list|)
expr_stmt|;
block|}
name|int
name|code
init|=
name|hessianInput
operator|.
name|read
argument_list|()
decl_stmt|;
if|if
condition|(
name|code
operator|!=
literal|'c'
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"expected 'c' in hessian input at "
operator|+
name|code
argument_list|)
throw|;
block|}
name|AbstractHessianOutput
name|hessianOutput
init|=
literal|null
decl_stmt|;
name|int
name|major
init|=
name|hessianInput
operator|.
name|read
argument_list|()
decl_stmt|;
comment|// useless read just get the stream in the right position.
name|int
name|minor
init|=
name|hessianInput
operator|.
name|read
argument_list|()
decl_stmt|;
if|if
condition|(
name|major
operator|>=
literal|2
condition|)
block|{
name|hessianOutput
operator|=
operator|new
name|Hessian2Output
argument_list|(
name|outputStream
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|hessianOutput
operator|=
operator|new
name|HessianOutput
argument_list|(
name|outputStream
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|serializerFactory
operator|!=
literal|null
condition|)
block|{
name|hessianOutput
operator|.
name|setSerializerFactory
argument_list|(
name|this
operator|.
name|serializerFactory
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|this
operator|.
name|skeleton
operator|.
name|invoke
argument_list|(
name|hessianInput
argument_list|,
name|hessianOutput
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Unexpected Exception thrown -- "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
block|}
comment|/**      * @return Returns the serializerFactory.      */
DECL|method|getSerializerFactory
specifier|public
name|SerializerFactory
name|getSerializerFactory
parameter_list|()
block|{
return|return
name|this
operator|.
name|serializerFactory
return|;
block|}
comment|/**      * @param serializerFactory The serializerFactory to set.      */
DECL|method|setSerializerFactory
specifier|public
name|void
name|setSerializerFactory
parameter_list|(
name|SerializerFactory
name|serializerFactory
parameter_list|)
block|{
name|this
operator|.
name|serializerFactory
operator|=
name|serializerFactory
expr_stmt|;
block|}
block|}
end_class

end_unit

