begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/* ====================================================================  * The Apache Software License, Version 1.1  *  * Copyright (c) 2001 The Apache Software Foundation.  All rights  * reserved.  *  * Redistribution and use in source and binary forms, with or without  * modification, are permitted provided that the following conditions  * are met:  *  * 1. Redistributions of source code must retain the above copyright  *    notice, this list of conditions and the following disclaimer.  *  * 2. Redistributions in binary form must reproduce the above copyright  *    notice, this list of conditions and the following disclaimer in  *    the documentation and/or other materials provided with the  *    distribution.  *  * 3. The end-user documentation included with the redistribution,  *    if any, must include the following acknowledgment:  *       "This product includes software developed by the  *        Apache Software Foundation (http://www.apache.org/)."  *    Alternately, this acknowledgment may appear in the software itself,  *    if and wherever such third-party acknowledgments normally appear.  *  * 4. The names "Apache" and "Apache Software Foundation" and  *    "Apache Lucene" must not be used to endorse or promote products  *    derived from this software without prior written permission. For  *    written permission, please contact apache@apache.org.  *  * 5. Products derived from this software may not be called "Apache",  *    "Apache Lucene", nor may "Apache" appear in their name, without  *    prior written permission of the Apache Software Foundation.  *  * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED  * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES  * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE  * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR  * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,  * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT  * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF  * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND  * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,  * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT  * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF  * SUCH DAMAGE.  * ====================================================================  *  * This software consists of voluntary contributions made by many  * individuals on behalf of the Apache Software Foundation.  For more  * information on the Apache Software Foundation, please see  *<http://www.apache.org/>.  */
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
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * filter class; gets IP Adresses from host names and forwards them to  * the other parts of the application  * since URLs cache their IP addresses themselves, and HTTP 1.1 needs the  * host names to be sent to the server, this class is not used anymore  * @version $Id$  */
end_comment

begin_class
DECL|class|DNSResolver
specifier|public
class|class
name|DNSResolver
implements|implements
name|MessageListener
block|{
DECL|field|ipCache
name|HashMap
name|ipCache
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
DECL|method|DNSResolver
specifier|public
name|DNSResolver
parameter_list|()
block|{     }
DECL|method|notifyAddedToMessageHandler
specifier|public
name|void
name|notifyAddedToMessageHandler
parameter_list|(
name|MessageHandler
name|m
parameter_list|)
block|{
name|this
operator|.
name|messageHandler
operator|=
name|m
expr_stmt|;
block|}
DECL|field|messageHandler
name|MessageHandler
name|messageHandler
decl_stmt|;
DECL|method|handleRequest
specifier|public
name|Message
name|handleRequest
parameter_list|(
name|Message
name|message
parameter_list|)
block|{
if|if
condition|(
name|message
operator|instanceof
name|URLMessage
condition|)
block|{
name|URL
name|url
init|=
operator|(
operator|(
name|URLMessage
operator|)
name|message
operator|)
operator|.
name|getUrl
argument_list|()
decl_stmt|;
name|String
name|host
init|=
name|url
operator|.
name|getHost
argument_list|()
decl_stmt|;
name|InetAddress
name|ip
decl_stmt|;
comment|/*InetAddress ip = (InetAddress)ipCache.get(host);              if(ip == null)             {                 */
try|try
block|{
name|ip
operator|=
name|InetAddress
operator|.
name|getByName
argument_list|(
name|host
argument_list|)
expr_stmt|;
comment|/*                     ipCache.put(host, ip);                     //System.out.println("DNSResolver: new Cache Entry \"" + host + "\" = \"" + ip.getHostAddress() + "\"");*/
block|}
catch|catch
parameter_list|(
name|UnknownHostException
name|e
parameter_list|)
block|{
name|ip
operator|=
literal|null
expr_stmt|;
return|return
literal|null
return|;
comment|//System.out.println("DNSResolver: unknown host \"" + host + "\"");
block|}
comment|/*}             else             {                //System.out.println("DNSResolver: Cache hit: " +  ip.getHostAddress());             }*/
comment|//((URLMessage)message).setIpAddress(ip);
block|}
return|return
name|message
return|;
block|}
block|}
end_class

end_unit

