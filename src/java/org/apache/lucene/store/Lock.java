begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.store
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
package|;
end_package

begin_comment
comment|/* ====================================================================  * The Apache Software License, Version 1.1  *  * Copyright (c) 2001 The Apache Software Foundation.  All rights  * reserved.  *  * Redistribution and use in source and binary forms, with or without  * modification, are permitted provided that the following conditions  * are met:  *  * 1. Redistributions of source code must retain the above copyright  *    notice, this list of conditions and the following disclaimer.  *  * 2. Redistributions in binary form must reproduce the above copyright  *    notice, this list of conditions and the following disclaimer in  *    the documentation and/or other materials provided with the  *    distribution.  *  * 3. The end-user documentation included with the redistribution,  *    if any, must include the following acknowledgment:  *       "This product includes software developed by the  *        Apache Software Foundation (http://www.apache.org/)."  *    Alternately, this acknowledgment may appear in the software itself,  *    if and wherever such third-party acknowledgments normally appear.  *  * 4. The names "Apache" and "Apache Software Foundation" and  *    "Apache Lucene" must not be used to endorse or promote products  *    derived from this software without prior written permission. For  *    written permission, please contact apache@apache.org.  *  * 5. Products derived from this software may not be called "Apache",  *    "Apache Lucene", nor may "Apache" appear in their name, without  *    prior written permission of the Apache Software Foundation.  *  * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED  * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES  * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE  * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR  * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,  * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT  * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF  * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND  * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,  * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT  * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF  * SUCH DAMAGE.  * ====================================================================  *  * This software consists of voluntary contributions made by many  * individuals on behalf of the Apache Software Foundation.  For more  * information on the Apache Software Foundation, please see  *<http://www.apache.org/>.  */
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

begin_comment
comment|/** An interprocess mutex lock.  *<p>Typical use might look like:<pre>  * new Lock.With(directory.makeLock("my.lock")) {  *     public Object doBody() {  *<it>... code to execute while locked ...</it>  *     }  *   }.run();  *</pre>  *  * @author Doug Cutting  * @see Directory#makeLock(String) */
end_comment

begin_class
DECL|class|Lock
specifier|public
specifier|abstract
class|class
name|Lock
block|{
comment|/** Attempt to obtain exclusive access.    *    * @return true iff exclusive access is obtained    */
DECL|method|obtain
specifier|public
specifier|abstract
name|boolean
name|obtain
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/** Release exclusive access. */
DECL|method|release
specifier|public
specifier|abstract
name|void
name|release
parameter_list|()
function_decl|;
comment|/** Utility class for executing code with exclusive access. */
DECL|class|With
specifier|public
specifier|abstract
specifier|static
class|class
name|With
block|{
DECL|field|lock
specifier|private
name|Lock
name|lock
decl_stmt|;
DECL|field|sleepInterval
specifier|private
name|int
name|sleepInterval
init|=
literal|1000
decl_stmt|;
DECL|field|maxSleeps
specifier|private
name|int
name|maxSleeps
init|=
literal|10
decl_stmt|;
comment|/** Constructs an executor that will grab the named lock. */
DECL|method|With
specifier|public
name|With
parameter_list|(
name|Lock
name|lock
parameter_list|)
block|{
name|this
operator|.
name|lock
operator|=
name|lock
expr_stmt|;
block|}
comment|/** Code to execute with exclusive access. */
DECL|method|doBody
specifier|protected
specifier|abstract
name|Object
name|doBody
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/** Calls {@link #doBody} while<it>lock</it> is obtained.  Blocks if lock      * cannot be obtained immediately.  Retries to obtain lock once per second      * until it is obtained, or until it has tried ten times. */
DECL|method|run
specifier|public
name|Object
name|run
parameter_list|()
throws|throws
name|IOException
block|{
name|boolean
name|locked
init|=
literal|false
decl_stmt|;
try|try
block|{
name|locked
operator|=
name|lock
operator|.
name|obtain
argument_list|()
expr_stmt|;
name|int
name|sleepCount
init|=
literal|0
decl_stmt|;
while|while
condition|(
operator|!
name|locked
condition|)
block|{
if|if
condition|(
operator|++
name|sleepCount
operator|==
name|maxSleeps
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Timed out waiting for: "
operator|+
name|lock
argument_list|)
throw|;
block|}
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|sleepInterval
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
name|locked
operator|=
name|lock
operator|.
name|obtain
argument_list|()
expr_stmt|;
block|}
return|return
name|doBody
argument_list|()
return|;
block|}
finally|finally
block|{
if|if
condition|(
name|locked
condition|)
name|lock
operator|.
name|release
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

