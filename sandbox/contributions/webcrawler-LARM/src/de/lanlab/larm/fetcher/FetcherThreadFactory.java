begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  ====================================================================  *  The Apache Software License, Version 1.1  *  *  Copyright (c) 2001 The Apache Software Foundation.  All rights  *  reserved.  *  *  Redistribution and use in source and binary forms, with or without  *  modification, are permitted provided that the following conditions  *  are met:  *  *  1. Redistributions of source code must retain the above copyright  *  notice, this list of conditions and the following disclaimer.  *  *  2. Redistributions in binary form must reproduce the above copyright  *  notice, this list of conditions and the following disclaimer in  *  the documentation and/or other materials provided with the  *  distribution.  *  *  3. The end-user documentation included with the redistribution,  *  if any, must include the following acknowledgment:  *  "This product includes software developed by the  *  Apache Software Foundation (http://www.apache.org/)."  *  Alternately, this acknowledgment may appear in the software itself,  *  if and wherever such third-party acknowledgments normally appear.  *  *  4. The names "Apache" and "Apache Software Foundation" and  *  "Apache Lucene" must not be used to endorse or promote products  *  derived from this software without prior written permission. For  *  written permission, please contact apache@apache.org.  *  *  5. Products derived from this software may not be called "Apache",  *  "Apache Lucene", nor may "Apache" appear in their name, without  *  prior written permission of the Apache Software Foundation.  *  *  THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED  *  WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES  *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE  *  DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR  *  ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,  *  SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT  *  LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF  *  USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND  *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,  *  OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT  *  OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF  *  SUCH DAMAGE.  *  ====================================================================  *  *  This software consists of voluntary contributions made by many  *  individuals on behalf of the Apache Software Foundation.  For more  *  information on the Apache Software Foundation, please see  *<http://www.apache.org/>.  */
end_comment

begin_package
DECL|package|de.lanlab.larm.fetcher
package|package
name|de
operator|.
name|lanlab
operator|.
name|larm
operator|.
name|fetcher
package|;
end_package

begin_import
import|import
name|de
operator|.
name|lanlab
operator|.
name|larm
operator|.
name|threads
operator|.
name|*
import|;
end_import

begin_import
import|import
name|de
operator|.
name|lanlab
operator|.
name|larm
operator|.
name|net
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * this factory simply creates fetcher threads. It's passed to the ThreadPool  * because the pool is creating the threads on its own  *  * @author    Administrator  * @created   14. Juni 2002  * @version   $Id: FetcherThreadFactory.java,v 1.2 2002/05/22 23:09:17  *      cmarschner Exp $  */
end_comment

begin_class
DECL|class|FetcherThreadFactory
specifier|public
class|class
name|FetcherThreadFactory
extends|extends
name|ThreadFactory
block|{
comment|//static int count = 0;
DECL|field|threadGroup
name|ThreadGroup
name|threadGroup
init|=
operator|new
name|ThreadGroup
argument_list|(
literal|"FetcherThreads"
argument_list|)
decl_stmt|;
DECL|field|hostManager
name|HostManager
name|hostManager
decl_stmt|;
comment|/**      * Constructor for the FetcherThreadFactory object      *      * @param hostManager  Description of the Parameter      */
DECL|method|FetcherThreadFactory
specifier|public
name|FetcherThreadFactory
parameter_list|(
name|HostManager
name|hostManager
parameter_list|)
block|{
name|this
operator|.
name|hostManager
operator|=
name|hostManager
expr_stmt|;
block|}
comment|/**      * Description of the Method      *      * @param count  Description of the Parameter      * @return       Description of the Return Value      */
DECL|method|createServerThread
specifier|public
name|ServerThread
name|createServerThread
parameter_list|(
name|int
name|count
parameter_list|)
block|{
name|ServerThread
name|newThread
init|=
operator|new
name|FetcherThread
argument_list|(
name|count
argument_list|,
name|threadGroup
argument_list|,
name|hostManager
argument_list|)
decl_stmt|;
name|newThread
operator|.
name|setPriority
argument_list|(
literal|4
argument_list|)
expr_stmt|;
return|return
name|newThread
return|;
block|}
block|}
end_class

end_unit

