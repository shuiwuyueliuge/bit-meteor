package cn.mayu.bt.protocol;

/**
 * peer_id
 * peer_id长20个字节。至于怎么将客户端和客户端版本信息编码成peer_id，现在主要有两种惯例：Azureus风格和Shadow风格。
 *
 * Azureus风格使用如下编码方式：’-’, 紧接着是2个字符的client id，再接着是4个数字的版本号，’-’，后面跟着随机数。
 *
 * 例如：'-AZ2060-'...
 *
 * 使用这种编码风格的知名客户端是：
 *
 * l  'AG' - Ares
 *
 * l  'A~' - Ares
 *
 * l  'AR' - Arctic
 *
 * l  'AT' - Artemis
 *
 * l  'AX' - BitPump
 *
 * l  'AZ' - Azureus
 *
 * l  'BB' - BitBuddy
 *
 * l  'BC' - BitComet
 *
 * l  'BF' - Bitflu
 *
 * l  'BG' - BTG (uses Rasterbar libtorrent)
 *
 * l  'BP' - BitTorrent Pro (Azureus + spyware)
 *
 * l  'BR' - BitRocket
 *
 * l  'BS' - BTSlave
 *
 * l  'BW' - BitWombat
 *
 * l  'BX' - ~Bittorrent X
 *
 * l  'CD' - Enhanced CTorrent
 *
 * l  'CT' - CTorrent
 *
 * l  'DE' - DelugeTorrent
 *
 * l  'DP' - Propagate Data Client
 *
 * l  'EB' - EBit
 *
 * l  'ES' - electric sheep
 *
 * l  'FC' - FileCroc
 *
 * l  'FT' - FoxTorrent
 *
 * l  'GS' - GSTorrent
 *
 * l  'HL' - Halite
 *
 * l  'HN' - Hydranode
 *
 * l  'KG' - KGet
 *
 * l  'KT' - KTorrent
 *
 * l  'LC' - LeechCraft
 *
 * l  'LH' - LH-ABC
 *
 * l  'LP' - Lphant
 *
 * l  'LT' - libtorrent
 *
 * l  'lt' - libTorrent
 *
 * l  'LW' - LimeWire
 *
 * l  'MO' - MonoTorrent
 *
 * l  'MP' - MooPolice
 *
 * l  'MR' - Miro
 *
 * l  'MT' - MoonlightTorrent
 *
 * l  'NX' - Net Transport
 *
 * l  'OT' - OmegaTorrent
 *
 * l  'PD' - Pando
 *
 * l  'qB' - qBittorrent
 *
 * l  'QD' - QQDownload
 *
 * l  'QT' - Qt 4 Torrent example
 *
 * l  'RT' - Retriever
 *
 * l  'RZ' - RezTorrent
 *
 * l  'S~' - Shareaza alpha/beta
 *
 * l  'SB' - ~Swiftbit
 *
 * l  'SS' - SwarmScope
 *
 * l  'ST' - SymTorrent
 *
 * l  'st' - sharktorrent
 *
 * l  'SZ' - Shareaza
 *
 * l  'TN' - TorrentDotNET
 *
 * l  'TR' - Transmission
 *
 * l  'TS' - Torrentstorm
 *
 * l  'TT' - TuoTu
 *
 * l  'UL' - uLeecher!
 *
 * l  'UM' - µTorrent for Mac
 *
 * l  'UT' - µTorrent
 *
 * l  'VG' - Vagaa
 *
 * l  'WT' - BitLet
 *
 * l  'WY' - FireTorrent
 *
 * l  'XL' - Xunlei
 *
 * l  'XT' - XanTorrent
 *
 * l  'XX' - Xtorrent
 *
 * l  'ZT' - ZipTorrent
 *
 * 另外还需要识别的客户端有：
 *
 * l  'BD' (例如: -BD0300-)
 *
 * l  'NP' (例如: -NP0201-)
 *
 * l  'SD' (例如: -SD0100-)
 *
 * l  'wF' (例如: -wF2200-)
 *
 * l  'hk' (例如: -hk0010-) 中国IP地址，IP address, unrequestedly sends info dict in message 0xA, reconnects immediately after being disconnected, reserved bytes = 01,01,01,01,00,00,02,01
 *
 * Shadow风格使用如下编码方式：一个用于客户端标识的ASCII字母数字，多达五个字符的版本号(如果少于5个，则以’-’填充)，紧接着是3个字符(通常是’---’，但也不总是这样)，最后跟着随机数。版本字符串中的每一个字符表示一个0到63的数字。'0'=0, ..., '9'=9, 'A'=10, ..., 'Z'=35, 'a'=36, ..., 'z'=61, '.'=62, '-'=63。
 *
 * 你可以在这找到关于shadow编码风格(包含关于版本字符串后的三个字符用法的习惯)的详细说明。
 *
 * 例如：用于Shadow 5.8.11的’S58B-----‘...
 *
 * 使用这种编码风格的知名客户端是：
 *
 * l  'A' - ABC
 *
 * l  'O' - Osprey Permaseed
 *
 * l  'Q' - BTQueue
 *
 * l  'R' - Tribler
 *
 * l  'S' - Shadow's client
 *
 * l  'T' - BitTornado
 *
 * l  'U' - UPnP NAT Bit Torrent
 *
 * Bram的客户端现在使用这种风格：'M3-4-2--' or 'M4-20-8-'。
 *
 * BitComet使用不同的编码风格。它的peer_id由4个ASCII字符’exbc’组成，接着是2个字节的x和y，最后是随机字符。版本号中的x在小数点前面，y是版本号后的两个数字。BitLord使用相同的方案，但是在版本号后面添加’LORD’。BitComet的一个非正式补丁曾经使用’FUTB’代替’exbc’。自版本0.59开始，BitComet peer id的编码使用Azureus风格。
 *
 * XBT客户端也使用其特有的风格。它的peer_id由三个大写字母’XBT’以及紧随其后的代表版本号的三个ASCII数字组成。如果客户端是debug版本，第七个字节是小写字符’d’，否则就是’-‘。接着就是’-‘，然后是随机数，大写和小写字母。例如：peer_id的开始部分为'XBT054d-'表明该客户端是版本号为0.5.4的debug版本。
 *
 * Opera 8预览版和Opera 9.x发行版使用以下的peer_id方案：开始的两个字符是’OP’，后面的四个数字是开发代号。接着的字符是随机的小写十六进制数字。
 *
 * MLdonkey使用如下的peer_id方案：开始的字符是’-ML’，后面跟着点式版本，然后就是一个’-’，最后跟着随机字符串。例如：'-ML2.7.2-kgjjfkd'。
 *
 * Bit on Wheels使用模式'-BOWxxx-yyyyyyyyyyyy'，其中y是随机的(大写字母)，x依赖于版本。如果版本为1.0.6，那么xxx = AOC。
 *
 * Queen Bee使用Bram的新风格：'Q1-0-0--' or 'Q1-10-0-'之后紧随着随机字节。
 *
 * BitTyrant是Azureus的一个分支，在它的1.1版本，其peer id使用'AZ2500BT' + 随机字节的方式。
 *
 * TorrenTopia版本1.90自称是或源自于Mainline 3.4.6。它的peer ID以'346------'开始。
 *
 * BitSpirit有几种编码peer ID的方式。一种模式是读取它的peer ID然后使用开始的八个字节作为它peer ID的基础来重新连接。它的实际ID使用'/0/3BS'(c 标记法)作为版本3.x的前四个字节，使用'/0/2BS'作为版本2.x的前四个字节。所有方式都使用'UDP0'作为结尾。
 *
 * Rufus使用它的十进制ASCII版本值作为开始的两个字节。第三个和第四个字节是'RS'。紧随其后的是用户的昵称和一些随机字节。
 *
 * C3 Torrent的peer ID以’-G3’开始，然后追加多达9个表示用户昵称的字符。
 *
 * FlashGet使用Azureus风格，但是前面字符是’FG’，没有’-’。版本 1.82.1002 仍然使用版本数字 '0180'。
 *
 * BT Next Evolution源自于BitTornado，但是试着模仿Azureus风格。结果是它的peer ID以’-NE’开始，接着是四个数字的版本号，最后就是以shadow peer id风格描述客户端类型的三个字符。
 *
 * 所有Peers使用一个特定于用户的字符串的Sha1哈希值，使用"AP" + version string + "-"代替开始的一些字符。
 *
 * Qvod的id以四个字母"QVOD"开始，接着是4个十进制数字的开发代号(目前是” 0054”)。最后的12个字符是随机的大写十六进制数字。中国有一个修改版，该版本以随机字节代替前四个字符。
 *
 * 许多客户端全部使用随机数或者随机数后面跟12个全0(像Bram客户端的老版本)。
 */
public interface PeerIdGenerator {

    String genPeerId();
}
