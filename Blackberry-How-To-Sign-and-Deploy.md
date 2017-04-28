# Blackberry: signing and deploying your application

#### Apply For Signature Keys and Install

The first step in the process is to complete the web form application including providing your credit card details and a 
personal 10 digit PIN number that you create. Think of it in the same way as the PIN number for your ATM card – 
it protects you from someone else acquiring your key and using it to sign their application. Once you submit the form, 
within 2 weeks you’ll receive three emails from Blackberry, each containing a different client key – RBB, RRT, and RCR.
The instructions that come with the keys are comprehensive and I didn’t run into trouble but I’ve included the Eclipse 
version below for reference. It is a two-step process for making a signed application. First you have to register the 
keys with RIM using the following process:


#### BlackBerry JDE Plug-in for Eclipse Users:

#### 1st Step – (BlackBerry Java Plug-in for Eclipse for OS (jde) version 6.0, plugin version 1.3)
1) Save all 3 .csi files in the same directory (each one will be sent in a separate email message).
  – client-RBB-206712107.csi
  – client-RRT-206712107.csi
  – client-RCR-206712107.csi
2) Start Eclipse.
3) Click on the BlackBerry menu and choose Install Signature Keys.
4) Select one of the 3 .csi files saved in step 1 and click Open.
5) Click “Yes” to create a new key pair file.
6) Type a password for your private key of at least 8 characters, and type it again to confirm. 
This is your private key password, which protects your private key. Please remember this password as you will be 
prompted for it each time signing is attempted or a signature key is installed.
7) Move your mouse to generate date for a new private key.
8) In the Registration PIN” field, type the PIN number that you supplied on the signature key order form.
9) In the Private Key password field, type the password created in step 6.
10) Click Register .
11) Click Exit .
12) Repeat this process for the other csi files.

This process creates key pairs located in your eclipse install directory under the plugins
(relevant files are sigtool.csk, sigtool.db, sigtool.set)

Request for Code Signature from Blackberry Signing Authority

#### 2nd Step – force your .cod file to be signed by requesting the code signature from the Blackberry Signing Authority 
Tool. 
This is done by navigating to the directory where your compiled application resides and double-clicking the cod file you’d 
like to request signing. If the install of your keys went correctly the signature tool will appear on your screen and it 
will show you the .cod files that were signed by each key.

If your application that you’re requesting signing for is large, you’ll notice that the .cod file is broken into many 
separate parts and each must be signed. When you click request, a popup will appear asking for your private key that you 
set up during the first step.

Enter your password and the request will be sent to RIM’s signing authority. Keep your eye on the status column of the 
signature tool to see if you successfully requested the signing. If you were successful, the status will show “Signed”.

Now you’re done, and you can load it onto your device!

#### Loading the signed Blackberry .cod application file on your device

1. Make sure you have the Blackberry desktop manager installed.  It will have to be running to connect to your device 
via USB to transfer the files.

2. Use the Blackberry JDE 5.0 or any other Blackberry JDE Component Package to load the signed .cod file.

3. Navigate to the “bin” directory of the Blackberry JDE that you installed.

4. You should see the “javaloader” program there – you will need to type javaloader -u load <cod location> to get it 
on your device.

Example:
C:\Program Files (x86)\Research In Motion>javaloader -u load 
C:\Users\Dan\bb_workspace_v5\SpaceFighter\deliverables\Standard\5.0.0\SpaceFighter.cod

You will see it being loaded onto your Blackberry device – when you’re done your screen should look like this:
RIM Wireless Handheld Java Loader
Copyright 2001-2009 Research In Motion Limited
Connected
Loading SpaceFighter                  Done
781092 bytes sent at ~1562184 bps
Disconnected

That’s it!  Now you should be able to view your application on your device.

#### Importing existing keys to a new eclipse environment (eclipse v1.3 to v1.1)

Depending on how many devices you are supporting, you will have different development environment for each one.  
For example, my code is signed to run on all blackberry OS versions 5.0 and 6.0 because I signed them in the 
appropriate environments.

For me, I started with 6.0, but had to import my code and keys into a 5.0 environment – this explains how to do that.

1. Copy the sigtool files mentioned earlier from your eclipse-v1.3 install directory to your eclipse v1.1 (plugins) 
directory.  (eclipse will need to have these present so it can find the private keys you already generated)

- On the menu, click Preferences.
- Expand the BlackBerry® Java® Plug-in item.
- Click Signature Tool.
- Click Import Existing Keys.
- Navigate to the location of the BlackBerry® development environment that you want to import the key from.
- Select the folder that you want to import the key from.
- Click OK.

Your keys are now imported to your new environment, and you can sign your application for another OS version.
You can just to go Project -> Blackberry -> Sign with Signature Tool
You will be prompted to enter your signing password, and you will see the entire project be signed on the screen.

Now you can upload that signed .cod file to a different OS Blackberry version!