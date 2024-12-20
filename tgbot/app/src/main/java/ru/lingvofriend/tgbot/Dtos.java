package ru.lingvofriend.tgbot;


class TelegramAuthDto {
  private String id;
  private String first_name;
  private String username;
  private String photo_url;
  private String auth_date;
  private String hash;

  public String getId() { return id; }
  public void setId(String id) { this.id = id; }
  public String getFirst_name() { return first_name; }
  public void setFirst_name(String first_name) { this.first_name = first_name; }
  public String getUsername() { return username; }
  public void setUsername(String username) { this.username = username; }
  public String getPhoto_url() { return photo_url; }
  public void setPhoto_url(String photo_url) { this.photo_url = photo_url; }
  public String getAuth_date() { return auth_date; }
  public void setAuth_date(String auth_date) { this.auth_date = auth_date; }
  public String getHash() { return hash; }
  public void setHash(String hash) { this.hash = hash; }
}

class MessageDto {
  private String role;
  private String text;

  public MessageDto(String role, String text) {
      this.role = role;
      this.text = text;
  }

  public String getRole() { return role; }
  public void setRole(String role) { this.role = role; }

  public String getText() { return text; }
  public void setText(String text) { this.text = text; }
}

class UserMessageDto {
  private MessageDto message;

  public UserMessageDto(MessageDto message) {
      this.message = message;
  }

  public MessageDto getMessage() { return message; }
  public void setMessage(MessageDto message) { this.message = message; }
}

class AuthUserDto {
  private String username;
  private String password;

  public AuthUserDto(String username, String password) {
      this.username = username;
      this.password = password;
  }

  public String getUsername() { return username; }
  public void setUsername(String username) { this.username = username; }
  public String getPassword() { return password; }
  public void setPassword(String password) { this.password = password; }
}

class RequestDto {
  private String username;
  private MessageDto message;

  public RequestDto(String username, MessageDto message) {
      this.username = username;
      this.message = message;
  }

  public String getUsername() { return username; }
  public void setUsername(String username) { this.username = username; }
  public MessageDto getMessage() { return message; }
  public void setMessage(MessageDto message) { this.message = message; }
}
