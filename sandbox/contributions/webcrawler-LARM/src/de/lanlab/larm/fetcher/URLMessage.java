begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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
name|util
operator|.
name|URLUtils
import|;
end_import

begin_comment
comment|/**  * represents a URL which is passed around in the messageHandler  */
end_comment

begin_class
DECL|class|URLMessage
specifier|public
class|class
name|URLMessage
implements|implements
name|Message
implements|,
name|Serializable
block|{
comment|/**      * the URL      */
DECL|field|url
specifier|protected
name|URL
name|url
decl_stmt|;
DECL|field|urlString
specifier|protected
name|String
name|urlString
decl_stmt|;
DECL|field|referer
specifier|protected
name|URL
name|referer
decl_stmt|;
DECL|field|refererString
specifier|protected
name|String
name|refererString
decl_stmt|;
DECL|field|isFrame
name|boolean
name|isFrame
decl_stmt|;
DECL|method|URLMessage
specifier|public
name|URLMessage
parameter_list|(
name|URL
name|url
parameter_list|,
name|URL
name|referer
parameter_list|,
name|boolean
name|isFrame
parameter_list|)
block|{
comment|//super();
name|this
operator|.
name|url
operator|=
name|url
expr_stmt|;
name|this
operator|.
name|urlString
operator|=
name|url
operator|!=
literal|null
condition|?
name|URLUtils
operator|.
name|toExternalFormNoRef
argument_list|(
name|url
argument_list|)
else|:
literal|null
expr_stmt|;
name|this
operator|.
name|referer
operator|=
name|referer
expr_stmt|;
name|this
operator|.
name|refererString
operator|=
name|referer
operator|!=
literal|null
condition|?
name|URLUtils
operator|.
name|toExternalFormNoRef
argument_list|(
name|referer
argument_list|)
else|:
literal|null
expr_stmt|;
name|this
operator|.
name|isFrame
operator|=
name|isFrame
expr_stmt|;
comment|//System.out.println("" + refererString + " -> " + urlString);
block|}
DECL|method|getUrl
specifier|public
name|URL
name|getUrl
parameter_list|()
block|{
return|return
name|this
operator|.
name|url
return|;
block|}
DECL|method|getReferer
specifier|public
name|URL
name|getReferer
parameter_list|()
block|{
return|return
name|this
operator|.
name|referer
return|;
block|}
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|urlString
return|;
block|}
DECL|method|getURLString
specifier|public
name|String
name|getURLString
parameter_list|()
block|{
return|return
name|urlString
return|;
block|}
DECL|method|getRefererString
specifier|public
name|String
name|getRefererString
parameter_list|()
block|{
return|return
name|refererString
return|;
block|}
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|url
operator|.
name|hashCode
argument_list|()
return|;
block|}
DECL|method|writeObject
specifier|private
name|void
name|writeObject
parameter_list|(
name|java
operator|.
name|io
operator|.
name|ObjectOutputStream
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeObject
argument_list|(
name|url
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeObject
argument_list|(
name|referer
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBoolean
argument_list|(
name|isFrame
argument_list|)
expr_stmt|;
block|}
DECL|method|readObject
specifier|private
name|void
name|readObject
parameter_list|(
name|java
operator|.
name|io
operator|.
name|ObjectInputStream
name|in
parameter_list|)
throws|throws
name|IOException
throws|,
name|ClassNotFoundException
block|{
name|url
operator|=
operator|(
name|URL
operator|)
name|in
operator|.
name|readObject
argument_list|()
expr_stmt|;
name|referer
operator|=
operator|(
name|URL
operator|)
name|in
operator|.
name|readObject
argument_list|()
expr_stmt|;
name|urlString
operator|=
name|url
operator|.
name|toExternalForm
argument_list|()
expr_stmt|;
name|refererString
operator|=
name|referer
operator|.
name|toExternalForm
argument_list|()
expr_stmt|;
name|isFrame
operator|=
name|in
operator|.
name|readBoolean
argument_list|()
expr_stmt|;
block|}
DECL|method|getInfo
specifier|public
name|String
name|getInfo
parameter_list|()
block|{
return|return
operator|(
name|referer
operator|!=
literal|null
condition|?
name|refererString
else|:
literal|"<start>"
operator|)
operator|+
literal|"\t"
operator|+
name|urlString
operator|+
literal|"\t"
operator|+
operator|(
name|isFrame
condition|?
literal|"1"
else|:
literal|"0"
operator|)
return|;
block|}
block|}
end_class

end_unit

