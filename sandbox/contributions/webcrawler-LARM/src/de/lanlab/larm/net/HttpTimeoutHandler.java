begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|de.lanlab.larm.net
package|package
name|de
operator|.
name|lanlab
operator|.
name|larm
operator|.
name|net
package|;
end_package

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|*
import|;
end_import

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
comment|/**  *  Description of the Class  *  *@author     cmarschn  *@created    2. Mai 2001  */
end_comment

begin_class
DECL|class|HttpTimeoutHandler
specifier|public
class|class
name|HttpTimeoutHandler
extends|extends
name|sun
operator|.
name|net
operator|.
name|www
operator|.
name|protocol
operator|.
name|http
operator|.
name|Handler
block|{
DECL|field|timeoutVal
name|int
name|timeoutVal
decl_stmt|;
DECL|field|fHUCT
name|HttpURLConnectionTimeout
name|fHUCT
decl_stmt|;
comment|/** 	 *  Constructor for the HttpTimeoutHandler object 	 * 	 *@param  iT  Description of Parameter 	 */
DECL|method|HttpTimeoutHandler
specifier|public
name|HttpTimeoutHandler
parameter_list|(
name|int
name|iT
parameter_list|)
block|{
name|timeoutVal
operator|=
name|iT
expr_stmt|;
block|}
comment|/** 	 *  Gets the Socket attribute of the HttpTimeoutHandler object 	 * 	 *@return    The Socket value 	 */
DECL|method|getSocket
specifier|public
name|Socket
name|getSocket
parameter_list|()
block|{
return|return
name|fHUCT
operator|.
name|getSocket
argument_list|()
return|;
block|}
comment|/** 	 *  Description of the Method 	 * 	 *@exception  Exception  Description of Exception 	 */
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|Exception
block|{
name|fHUCT
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/** 	 *  Description of the Method 	 * 	 *@param  u                Description of Parameter 	 *@return                  Description of the Returned Value 	 *@exception  IOException  Description of Exception 	 */
DECL|method|openConnection
specifier|protected
name|java
operator|.
name|net
operator|.
name|URLConnection
name|openConnection
parameter_list|(
name|URL
name|u
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|fHUCT
operator|=
operator|new
name|HttpURLConnectionTimeout
argument_list|(
name|u
argument_list|,
name|this
argument_list|,
name|timeoutVal
argument_list|)
return|;
block|}
comment|/** 	 *  Gets the Proxy attribute of the HttpTimeoutHandler object 	 * 	 *@return    The Proxy value 	 */
DECL|method|getProxy
name|String
name|getProxy
parameter_list|()
block|{
return|return
name|proxy
return|;
comment|// breaking encapsulation
block|}
comment|/** 	 *  Gets the ProxyPort attribute of the HttpTimeoutHandler object 	 * 	 *@return    The ProxyPort value 	 */
DECL|method|getProxyPort
name|int
name|getProxyPort
parameter_list|()
block|{
return|return
name|proxyPort
return|;
comment|// breaking encapsulation
block|}
block|}
end_class

end_unit

