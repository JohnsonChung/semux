/**
 * Copyright (c) 2017 The Semux Developers
 * 
 * Distributed under the MIT software license, see the accompanying file
 * LICENSE or https://opensource.org/licenses/mit-license.php
 */
package org.semux.cli;

import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.doCallRealMethod;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;
import static org.semux.LoggingAppender.info;

import java.security.KeyPair;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.cli.ParseException;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;
import org.junit.contrib.java.lang.system.SystemErrRule;
import org.junit.contrib.java.lang.system.SystemOutRule;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.semux.Kernel;
import org.semux.LoggingAppender;
import org.semux.config.DevNetConfig;
import org.semux.config.MainNetConfig;
import org.semux.config.TestNetConfig;
import org.semux.core.Wallet;
import org.semux.crypto.EdDSA;
import org.semux.crypto.Hex;
import org.semux.message.CLIMessages;
import org.semux.util.SystemUtil;
import org.semux.util.SystemUtil.OsName;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.LoggingEvent;
import net.i2p.crypto.eddsa.KeyPairGenerator;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ SystemUtil.class, Kernel.class, SemuxCLI.class })
public class SemuxCLITest {

    @Rule
    public final ExpectedSystemExit exit = ExpectedSystemExit.none();

    @Rule
    public final SystemErrRule systemErrRule = new SystemErrRule().enableLog();

    @Rule
    public final SystemOutRule systemOutRule = new SystemOutRule().enableLog();

    @Before
    public void setUp() {
        LoggingAppender.clear();
        LoggingAppender.prepare(Level.INFO, SemuxCLI.class, LoggerFactory.getLogger(SemuxCLI.class));
    }

    @After
    public void tearDown() {
        LoggingAppender.prepare(Level.OFF, null, null);
    }

    @Test
    public void testMain() throws Exception {
        String[] args = { "arg1", "arg2" };

        SemuxCLI semuxCLI = mock(SemuxCLI.class);
        whenNew(SemuxCLI.class).withAnyArguments().thenReturn(semuxCLI);

        SemuxCLI.main(args);

        verify(semuxCLI).start(args);
    }

    @Test
    public void testHelp() throws ParseException {
        SemuxCLI semuxCLI = spy(new SemuxCLI());
        semuxCLI.start(new String[] { "--help" });
        verify(semuxCLI).printHelp();
    }

    @Test
    public void testVersion() throws ParseException {
        SemuxCLI semuxCLI = spy(new SemuxCLI());
        semuxCLI.start(new String[] { "--version" });
        verify(semuxCLI).printVersion();
    }

    @Test
    public void testMainNetwork() throws ParseException {
        SemuxCLI semuxCLI = spy(new SemuxCLI());

        // mock accounts
        List<EdDSA> accounts = new ArrayList<>();
        EdDSA account = new EdDSA();
        accounts.add(account);

        // mock wallet
        Wallet wallet = mock(Wallet.class);
        when(wallet.unlock("oldpassword")).thenReturn(true);
        when(wallet.getAccounts()).thenReturn(accounts);
        when(semuxCLI.loadWallet()).thenReturn(wallet);

        // mock SystemUtil
        mockStatic(SystemUtil.class);
        when(SystemUtil.readPassword()).thenReturn("oldpassword");
        when(SystemUtil.getOsName()).thenReturn(OsName.LINUX);
        when(SystemUtil.getOsArch()).thenReturn("amd64");
        when(SystemUtil.readPassword()).thenReturn("oldpassword");
        doReturn(null).when(semuxCLI).startKernel(any(), any(), any());
        semuxCLI.start(new String[] { "--network", "mainnet" });

        assertTrue(semuxCLI.getConfig() instanceof MainNetConfig);
    }

    @Test
    public void testMainNetworkNotSpecified() throws ParseException {

        SemuxCLI semuxCLI = spy(new SemuxCLI());

        // mock accounts
        List<EdDSA> accounts = new ArrayList<>();
        EdDSA account = new EdDSA();
        accounts.add(account);

        // mock wallet
        Wallet wallet = mock(Wallet.class);
        when(wallet.unlock("oldpassword")).thenReturn(true);
        when(wallet.getAccounts()).thenReturn(accounts);
        when(semuxCLI.loadWallet()).thenReturn(wallet);

        // mock SystemUtil
        mockStatic(SystemUtil.class);
        when(SystemUtil.readPassword()).thenReturn("oldpassword");
        when(SystemUtil.getOsName()).thenReturn(OsName.LINUX);
        when(SystemUtil.getOsArch()).thenReturn("amd64");
        when(SystemUtil.readPassword()).thenReturn("oldpassword");
        doReturn(null).when(semuxCLI).startKernel(any(), any(), any());
        semuxCLI.start();

        assertTrue(semuxCLI.getConfig() instanceof MainNetConfig);

    }

    @Test
    public void testTestNetwork() throws ParseException {

        SemuxCLI semuxCLI = spy(new SemuxCLI());

        // mock accounts
        List<EdDSA> accounts = new ArrayList<>();
        EdDSA account = new EdDSA();
        accounts.add(account);

        // mock wallet
        Wallet wallet = mock(Wallet.class);
        when(wallet.unlock("oldpassword")).thenReturn(true);
        when(wallet.getAccounts()).thenReturn(accounts);
        when(semuxCLI.loadWallet()).thenReturn(wallet);

        // mock SystemUtil
        mockStatic(SystemUtil.class);
        when(SystemUtil.readPassword()).thenReturn("oldpassword");
        when(SystemUtil.getOsName()).thenReturn(OsName.LINUX);
        when(SystemUtil.getOsArch()).thenReturn("amd64");
        when(SystemUtil.readPassword()).thenReturn("oldpassword");
        doReturn(null).when(semuxCLI).startKernel(any(), any(), any());
        semuxCLI.start(new String[] { "--network", "testnet" });

        assertTrue(semuxCLI.getConfig() instanceof TestNetConfig);

    }

    @Test
    public void testDevNetwork() throws ParseException {

        SemuxCLI semuxCLI = spy(new SemuxCLI());

        // mock accounts
        List<EdDSA> accounts = new ArrayList<>();
        EdDSA account = new EdDSA();
        accounts.add(account);

        // mock wallet
        Wallet wallet = mock(Wallet.class);
        when(wallet.unlock("oldpassword")).thenReturn(true);
        when(wallet.getAccounts()).thenReturn(accounts);
        when(semuxCLI.loadWallet()).thenReturn(wallet);

        // mock SystemUtil
        mockStatic(SystemUtil.class);
        when(SystemUtil.readPassword()).thenReturn("oldpassword");
        when(SystemUtil.getOsName()).thenReturn(OsName.LINUX);
        when(SystemUtil.getOsArch()).thenReturn("amd64");
        when(SystemUtil.readPassword()).thenReturn("oldpassword");
        doReturn(null).when(semuxCLI).startKernel(any(), any(), any());
        semuxCLI.start(new String[] { "--network", "devnet" });

        assertTrue(semuxCLI.getConfig() instanceof DevNetConfig);

    }

    @Test
    public void testStartKernelWithEmptyWallet() throws Exception {
        SemuxCLI semuxCLI = spy(new SemuxCLI());

        // mock wallet
        Wallet wallet = mock(Wallet.class);
        when(wallet.unlock("oldpassword")).thenReturn(true);
        doReturn(new ArrayList<EdDSA>(), // returns empty wallet
                Collections.singletonList(new EdDSA()) // returns wallet with a newly created account
        ).when(wallet).getAccounts();
        when(wallet.addAccount(any(EdDSA.class))).thenReturn(true);
        when(wallet.flush()).thenReturn(true);

        // mock CLI
        when(semuxCLI.loadWallet()).thenReturn(wallet);
        doReturn(null).when(semuxCLI).startKernel(any(), any(), any());

        // mock new account
        EdDSA newAccount = new EdDSA();
        whenNew(EdDSA.class).withAnyArguments().thenReturn(newAccount);

        // mock SystemUtil
        mockStatic(SystemUtil.class);
        when(SystemUtil.readPassword()).thenReturn("oldpassword");
        when(SystemUtil.getOsName()).thenReturn(OsName.LINUX);
        when(SystemUtil.getOsArch()).thenReturn("amd64");

        // execution
        semuxCLI.start();

        // verifies that a new account is added the empty wallet
        verify(wallet).unlock("oldpassword");
        verify(wallet, times(2)).getAccounts();
        verify(wallet).addAccount(any(EdDSA.class));
        verify(wallet).flush();

        // verifies that kernel starts
        verify(semuxCLI).startKernel(any(), any(), any());

        // assert outputs
        List<LoggingEvent> logs = LoggingAppender.events();
        assertThat(logs, hasItem(info(CLIMessages.get("NewAccountCreatedForAddress", newAccount.toAddressString()))));
    }

    @Test
    public void testAccountActionList() throws ParseException {
        SemuxCLI semuxCLI = spy(new SemuxCLI());
        Mockito.doNothing().when(semuxCLI).listAccounts();
        semuxCLI.start(new String[] { "--account", "list" });
        verify(semuxCLI).listAccounts();
    }

    @Test
    public void testAccountActionCreate() throws ParseException {
        SemuxCLI semuxCLI = spy(new SemuxCLI());
        Mockito.doNothing().when(semuxCLI).createAccount();
        semuxCLI.start(new String[] { "--account", "create" });
        verify(semuxCLI).createAccount();
    }

    @Test
    public void testCreateAccount() throws Exception {
        SemuxCLI semuxCLI = spy(new SemuxCLI());

        // mock wallet
        Wallet wallet = mock(Wallet.class);
        when(wallet.unlock("oldpassword")).thenReturn(true);
        when(wallet.addAccount(any(EdDSA.class))).thenReturn(true);
        when(wallet.flush()).thenReturn(true);
        when(semuxCLI.loadWallet()).thenReturn(wallet);

        // mock account
        EdDSA newAccount = new EdDSA();
        whenNew(EdDSA.class).withAnyArguments().thenReturn(newAccount);

        // mock SystemUtil
        mockStatic(SystemUtil.class);
        when(SystemUtil.readPassword()).thenReturn("oldpassword");

        // execution
        semuxCLI.createAccount();

        // verification
        verify(wallet).addAccount(any(EdDSA.class));
        verify(wallet).flush();

        // assert outputs
        List<LoggingEvent> logs = LoggingAppender.events();
        assertThat(logs, hasItem(info(CLIMessages.get("NewAccountCreatedForAddress", newAccount.toAddressString()))));
        assertThat(logs, hasItem(info(CLIMessages.get("PublicKey", Hex.encode(newAccount.getPublicKey())))));
        assertThat(logs, hasItem(info(CLIMessages.get("PrivateKey", Hex.encode(newAccount.getPrivateKey())))));
    }

    @Test
    public void testListAccounts() throws ParseException {
        SemuxCLI semuxCLI = spy(new SemuxCLI());

        // mock accounts
        List<EdDSA> accounts = new ArrayList<>();
        EdDSA account = new EdDSA();
        accounts.add(account);

        // mock wallet
        Wallet wallet = mock(Wallet.class);
        when(wallet.unlock("oldpassword")).thenReturn(true);
        when(wallet.getAccounts()).thenReturn(accounts);
        when(semuxCLI.loadWallet()).thenReturn(wallet);

        // mock SystemUtil
        mockStatic(SystemUtil.class);
        when(SystemUtil.readPassword()).thenReturn("oldpassword");

        // execution
        semuxCLI.listAccounts();

        // verification
        verify(wallet).getAccounts();

        // assert outputs
        List<LoggingEvent> logs = LoggingAppender.events();
        assertThat(logs, hasItem(info(CLIMessages.get("ListAccountItem", 0, account.toAddressString()))));
    }

    @Test
    public void testChangePassword() throws ParseException {
        SemuxCLI semuxCLI = spy(new SemuxCLI());

        // mock wallet
        Wallet wallet = mock(Wallet.class);
        when(wallet.unlock("oldpassword")).thenReturn(true);
        when(wallet.flush()).thenReturn(true);
        when(semuxCLI.loadWallet()).thenReturn(wallet);

        // mock SystemUtil
        mockStatic(SystemUtil.class);
        when(SystemUtil.readPassword()).thenReturn("oldpassword");
        Mockito.when(SystemUtil.readPassword(anyString())).thenReturn("newpassword");

        // execution
        semuxCLI.changePassword();

        // verification
        verify(wallet).changePassword("newpassword");
        verify(wallet).flush();
    }

    @Test
    public void testDumpPrivateKey() {
        SemuxCLI semuxCLI = spy(new SemuxCLI());

        // mock account
        EdDSA account = spy(new EdDSA());
        String address = account.toAddressString();
        byte[] addressBytes = account.toAddress();

        // mock wallet
        Wallet wallet = mock(Wallet.class);
        when(wallet.unlock("oldpassword")).thenReturn(true);
        when(semuxCLI.loadWallet()).thenReturn(wallet);
        when(wallet.getAccount(addressBytes)).thenReturn(account);

        // mock SystemUtil
        mockStatic(SystemUtil.class);
        when(SystemUtil.readPassword()).thenReturn("oldpassword");

        // execution
        semuxCLI.dumpPrivateKey(address);

        // verification
        verify(wallet).getAccount(addressBytes);
        verify(account).getPrivateKey();
        assertEquals(CLIMessages.get("PrivateKeyIs", Hex.encode(account.getPrivateKey())),
                systemOutRule.getLog().trim());
    }

    @Test
    public void testDumpPrivateKeyNotFound() throws Exception {
        SemuxCLI semuxCLI = spy(new SemuxCLI());

        // mock address
        String address = "c583b6ad1d1cccfc00ae9113db6408f022822b20";
        byte[] addressBytes = Hex.decode0x(address);

        // mock wallet
        Wallet wallet = mock(Wallet.class);
        when(wallet.unlock("oldpassword")).thenReturn(true);
        when(semuxCLI.loadWallet()).thenReturn(wallet);
        when(wallet.getAccount(addressBytes)).thenReturn(null);

        // mock SystemUtil
        mockStatic(SystemUtil.class);
        when(SystemUtil.readPassword()).thenReturn("oldpassword");
        doCallRealMethod().when(SystemUtil.class, "exit", any(Integer.class));

        // expect System.exit(1)
        exit.expectSystemExitWithStatus(1);

        // execution
        semuxCLI.dumpPrivateKey(address);
    }

    @Test
    public void testImportPrivateKeyExisted() throws Exception {
        SemuxCLI semuxCLI = spy(new SemuxCLI());

        // mock private key
        KeyPairGenerator gen = new KeyPairGenerator();
        KeyPair keypair = gen.generateKeyPair();
        String key = Hex.encode(keypair.getPrivate().getEncoded());

        // mock wallet
        Wallet wallet = mock(Wallet.class);
        when(wallet.unlock("oldpassword")).thenReturn(true);
        when(semuxCLI.loadWallet()).thenReturn(wallet);
        when(wallet.addAccount(any(EdDSA.class))).thenReturn(false);

        // mock SystemUtil
        mockStatic(SystemUtil.class);
        when(SystemUtil.readPassword()).thenReturn("oldpassword");
        doCallRealMethod().when(SystemUtil.class, "exit", any(Integer.class));

        // expectation
        exit.expectSystemExitWithStatus(1);

        // execution
        semuxCLI.importPrivateKey(key);
    }

    @Test
    public void testImportPrivateKeyFailedToFlushWalletFile() throws Exception {
        SemuxCLI semuxCLI = spy(new SemuxCLI());

        // mock private key
        KeyPairGenerator gen = new KeyPairGenerator();
        KeyPair keypair = gen.generateKeyPair();
        String key = Hex.encode(keypair.getPrivate().getEncoded());

        // mock wallet
        Wallet wallet = mock(Wallet.class);
        when(wallet.unlock("oldpassword")).thenReturn(true);
        when(semuxCLI.loadWallet()).thenReturn(wallet);
        when(wallet.addAccount(any(EdDSA.class))).thenReturn(true);
        when(wallet.flush()).thenReturn(false);

        // mock SystemUtil
        mockStatic(SystemUtil.class);
        when(SystemUtil.readPassword()).thenReturn("oldpassword");
        doCallRealMethod().when(SystemUtil.class, "exit", any(Integer.class));

        // expectation
        exit.expectSystemExitWithStatus(2);

        // execution
        semuxCLI.importPrivateKey(key);
    }

    @Test
    public void testImportPrivateKey() {
        SemuxCLI semuxCLI = spy(new SemuxCLI());

        // mock private key
        final String key = "302e020100300506032b657004220420bd2f24b259aac4bfce3792c31d0f62a7f28b439c3e4feb97050efe5fe254f2af";

        // mock wallet
        Wallet wallet = mock(Wallet.class);
        when(wallet.unlock("oldpassword")).thenReturn(true);
        when(semuxCLI.loadWallet()).thenReturn(wallet);
        when(wallet.addAccount(any(EdDSA.class))).thenReturn(true);
        when(wallet.flush()).thenReturn(true);

        // mock SystemUtil
        mockStatic(SystemUtil.class);
        when(SystemUtil.readPassword()).thenReturn("oldpassword");

        // execution
        semuxCLI.importPrivateKey(key);

        // assertions
        List<LoggingEvent> logs = LoggingAppender.events();
        assertThat(logs, hasItem(info(CLIMessages.get("PrivateKeyImportedSuccessfully"))));
        assertThat(logs, hasItem(info(CLIMessages.get("Address", "0680a919c78faa59b127014b6181979ae0a62dbd"))));
        assertThat(logs, hasItem(info(CLIMessages.get("PrivateKey", key))));
    }
}