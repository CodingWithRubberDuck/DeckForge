package com.HolgersDream.Deckforge.domain;

public class User {
    final int MAX_PASSWORD = 400;
    private int userId;
    private String name;
    private String email;
    private String password;
    private Role role;


    //Base Chain Constructor kald
    //Constructor uden password eller email
    public User(int userId, String name, Role role){
        if (userId < 0){
            throw new IllegalArgumentException("Brugeren har ikke et gyldigt id");
        }
        this.userId = userId;
        
        final int MAX_NAME = 150;
        if (name == null || name.isBlank()){
            throw new IllegalArgumentException("Brugeren har ikke et gyldigt navn");
        } else if (name.length() > MAX_NAME) {
            throw new IllegalArgumentException("Et navn kan maksimalt være " + MAX_NAME + " tegn langt");
        }
        this.name = name.trim();

        if (role == null){
            throw new IllegalArgumentException("Brugeren har ikke en gyldig rolle");
        }
        this.role = role;
    }



    //Konstruktør med alle værdier
    public User(int userId, String name, String email, String password, Role role) {
        //Chain constructor kald
        this(userId, name, role);

        final int MAX_EMAIL = 150;
        if (email == null || email.isBlank()){
            throw new IllegalArgumentException("Brugeren har ikke en gyldig email");
        } else if (email.length() > MAX_EMAIL) {
            throw new IllegalArgumentException("En email kan maksimalt være " + MAX_EMAIL + " tegn lang");
        }
        this.email = email.trim();
        
        if (password == null || password.isBlank()){
            throw new IllegalArgumentException("Brugeren har ikke et gyldigt kodeord");
        } else if (password.length() > MAX_PASSWORD) {
            throw new IllegalArgumentException("Et kodeord kan maksimalt være " + MAX_PASSWORD + " tegn langt");
        }
        this.password = password;
    }


    // Change with rules
    public void changePassword(String newPassword){
        if (newPassword == null || newPassword.isBlank()){
            throw new IllegalArgumentException();
        } else if (password.length() > MAX_PASSWORD) {
            throw new IllegalArgumentException("Et kodeord kan maksimalt være " + MAX_PASSWORD + " tegn langt");
        }
        this.password = newPassword;
    }




    /// Getters
    public int getUserId(){
        return userId;
    }
    public String getName(){
        return name;
    }
    public String getEmail(){
        return email;
    }
    public String getPassword() {
        return password;
    }
    public Role getRole(){
        return role;
    }
}
