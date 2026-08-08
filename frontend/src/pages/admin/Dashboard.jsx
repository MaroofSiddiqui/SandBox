import { useNavigate } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";

function Dashboard() {

    const navigate = useNavigate();
    const { user, logout } = useAuth();

    // ============================================================
    // LOGOUT
    // ============================================================

    const handleLogout = async () => {

        try {

            if (typeof logout === "function") {
                await logout();
            }

        } catch (error) {

            console.error(
                "Logout error:",
                error
            );

        } finally {

            localStorage.removeItem("token");

            navigate(
                "/login",
                {
                    replace: true
                }
            );
        }
    };


    // ============================================================
    // NAVIGATION
    // ============================================================

    const goTo = (path) => {
        navigate(path);
    };


    // ============================================================
    // MAIN DASHBOARD
    // ============================================================

    return (

        <div
            style={{
                minHeight: "100vh",
                background: "#f1f5f9",
                display: "flex",
                fontFamily:
                    "Arial, sans-serif",
                color: "#0f172a"
            }}
        >

            {/* ====================================================
                SIDEBAR
               ==================================================== */}

            <aside
                style={{
                    width: "270px",
                    background:
                        "linear-gradient(180deg,#0f172a,#172554)",
                    color: "#ffffff",
                    padding: "28px 20px",
                    boxSizing: "border-box",
                    display: "flex",
                    flexDirection: "column",
                    position: "fixed",
                    top: 0,
                    left: 0,
                    bottom: 0
                }}
            >

                {/* =================================================
                    LOGO
                   ================================================= */}

                <div
                    style={{
                        display: "flex",
                        alignItems: "center",
                        gap: "13px",
                        marginBottom: "35px"
                    }}
                >

                    <div
                        style={{
                            width: "48px",
                            height: "48px",
                            borderRadius: "12px",
                            background:
                                "linear-gradient(135deg,#2563eb,#60a5fa)",
                            display: "flex",
                            alignItems: "center",
                            justifyContent: "center",
                            fontSize: "25px",
                            fontWeight: "800",
                            boxShadow:
                                "0 8px 20px rgba(37,99,235,0.3)"
                        }}
                    >
                        S
                    </div>

                    <div>

                        <div
                            style={{
                                fontSize: "21px",
                                fontWeight: "800"
                            }}
                        >
                            SandBox
                        </div>

                        <div
                            style={{
                                fontSize: "11px",
                                color: "#93c5fd",
                                letterSpacing:
                                    "0.7px"
                            }}
                        >
                            ADMIN CONSOLE
                        </div>

                    </div>

                </div>


                {/* =================================================
                    NAVIGATION
                   ================================================= */}

                <div
                    style={{
                        display: "flex",
                        flexDirection: "column",
                        gap: "8px"
                    }}
                >

                    {/* DASHBOARD */}

                    <button
                        onClick={() =>
                            goTo("/admin/dashboard")
                        }
                        style={{
                            width: "100%",
                            padding: "13px 15px",
                            borderRadius: "10px",
                            border: "none",
                            background:
                                "rgba(59,130,246,0.25)",
                            color: "#ffffff",
                            display: "flex",
                            alignItems: "center",
                            gap: "12px",
                            fontSize: "15px",
                            fontWeight: "700",
                            cursor: "pointer",
                            textAlign: "left"
                        }}
                    >
                        <span
                            style={{
                                width: "22px"
                            }}
                        >
                            ▦
                        </span>

                        Dashboard
                    </button>


                    {/* ORGANIZATIONS */}

                    <button
                        onClick={() =>
                            goTo("/admin/organizations")
                        }
                        style={{
                            width: "100%",
                            padding: "13px 15px",
                            borderRadius: "10px",
                            border: "none",
                            background: "transparent",
                            color: "#cbd5e1",
                            display: "flex",
                            alignItems: "center",
                            gap: "12px",
                            fontSize: "15px",
                            fontWeight: "600",
                            cursor: "pointer",
                            textAlign: "left"
                        }}
                    >
                        <span
                            style={{
                                width: "22px"
                            }}
                        >
                            ▣
                        </span>

                        Organizations
                    </button>


                    {/* HR MANAGEMENT */}

                    <button
                        onClick={() =>
                            goTo("/admin/hrs")
                        }
                        style={{
                            width: "100%",
                            padding: "13px 15px",
                            borderRadius: "10px",
                            border: "none",
                            background: "transparent",
                            color: "#cbd5e1",
                            display: "flex",
                            alignItems: "center",
                            gap: "12px",
                            fontSize: "15px",
                            fontWeight: "600",
                            cursor: "pointer",
                            textAlign: "left"
                        }}
                    >
                        <span
                            style={{
                                width: "22px"
                            }}
                        >
                            ♙
                        </span>

                        HR Management
                    </button>


                    {/* SUBSCRIPTIONS */}

                    <button
                        onClick={() =>
                            goTo(
                                "/admin/subscriptions"
                            )
                        }
                        style={{
                            width: "100%",
                            padding: "13px 15px",
                            borderRadius: "10px",
                            border: "none",
                            background: "transparent",
                            color: "#cbd5e1",
                            display: "flex",
                            alignItems: "center",
                            gap: "12px",
                            fontSize: "15px",
                            fontWeight: "600",
                            cursor: "pointer",
                            textAlign: "left"
                        }}
                    >
                        <span
                            style={{
                                width: "22px"
                            }}
                        >
                            ◈
                        </span>

                        Subscriptions
                    </button>


                    {/* PAYMENTS */}

                    <button
                        onClick={() =>
                            goTo("/admin/payments")
                        }
                        style={{
                            width: "100%",
                            padding: "13px 15px",
                            borderRadius: "10px",
                            border: "none",
                            background: "transparent",
                            color: "#cbd5e1",
                            display: "flex",
                            alignItems: "center",
                            gap: "12px",
                            fontSize: "15px",
                            fontWeight: "600",
                            cursor: "pointer",
                            textAlign: "left"
                        }}
                    >
                        <span
                            style={{
                                width: "22px"
                            }}
                        >
                            ₹
                        </span>

                        Payments
                    </button>

                </div>


                {/* =================================================
                    SPACER
                   ================================================= */}

                <div
                    style={{
                        flex: 1
                    }}
                />


                {/* =================================================
                    ADMIN PROFILE
                   ================================================= */}

                <div
                    style={{
                        borderTop:
                            "1px solid rgba(255,255,255,0.12)",
                        paddingTop: "20px",
                        marginBottom: "15px"
                    }}
                >

                    <div
                        style={{
                            display: "flex",
                            alignItems: "center",
                            gap: "12px"
                        }}
                    >

                        <div
                            style={{
                                width: "42px",
                                height: "42px",
                                borderRadius: "50%",
                                background:
                                    "#2563eb",
                                display: "flex",
                                alignItems:
                                    "center",
                                justifyContent:
                                    "center",
                                fontWeight: "800",
                                fontSize: "17px"
                            }}
                        >
                            {(user?.name ||
                                "A")
                                .charAt(0)
                                .toUpperCase()}
                        </div>

                        <div>

                            <div
                                style={{
                                    fontWeight: "700",
                                    fontSize: "14px"
                                }}
                            >
                                {user?.name ||
                                    "Super Admin"}
                            </div>

                            <div
                                style={{
                                    color: "#93c5fd",
                                    fontSize: "11px",
                                    marginTop: "2px"
                                }}
                            >
                                SUPER_ADMIN
                            </div>

                        </div>

                    </div>

                </div>


                {/* =================================================
                    LOGOUT
                   ================================================= */}

                <button
                    onClick={handleLogout}
                    style={{
                        width: "100%",
                        padding: "12px",
                        borderRadius: "9px",
                        border:
                            "1px solid rgba(255,255,255,0.15)",
                        background:
                            "rgba(255,255,255,0.05)",
                        color: "#ffffff",
                        fontSize: "15px",
                        fontWeight: "600",
                        cursor: "pointer"
                    }}
                >
                    ↪ &nbsp; Logout
                </button>

            </aside>


            {/* ====================================================
                MAIN CONTENT
               ==================================================== */}

            <main
                style={{
                    marginLeft: "270px",
                    width:
                        "calc(100% - 270px)",
                    padding:
                        "30px 40px",
                    boxSizing:
                        "border-box"
                }}
            >

                {/* =================================================
                    TOP BAR
                   ================================================= */}

                <div
                    style={{
                        background: "#ffffff",
                        borderRadius: "14px",
                        padding: "15px 20px",
                        display: "flex",
                        alignItems: "center",
                        justifyContent:
                            "space-between",
                        border:
                            "1px solid #e2e8f0",
                        marginBottom: "30px"
                    }}
                >

                    {/* SEARCH */}

                    <div
                        style={{
                            display: "flex",
                            alignItems:
                                "center",
                            background:
                                "#f8fafc",
                            border:
                                "1px solid #e2e8f0",
                            borderRadius:
                                "9px",
                            width: "330px",
                            padding:
                                "10px 13px"
                        }}
                    >

                        <span
                            style={{
                                color: "#64748b",
                                marginRight:
                                    "8px"
                            }}
                        >
                            ⌕
                        </span>

                        <input
                            type="text"
                            placeholder="Search..."
                            style={{
                                border: "none",
                                outline: "none",
                                background:
                                    "transparent",
                                width: "100%",
                                fontSize:
                                    "14px"
                            }}
                        />

                    </div>


                    {/* ADMIN */}

                    <div
                        style={{
                            display: "flex",
                            alignItems:
                                "center",
                            gap: "12px"
                        }}
                    >

                        <div
                            style={{
                                textAlign: "right"
                            }}
                        >

                            <div
                                style={{
                                    fontWeight:
                                        "700",
                                    fontSize:
                                        "14px"
                                }}
                            >
                                {user?.name ||
                                    "Super Admin"}
                            </div>

                            <div
                                style={{
                                    color:
                                        "#64748b",
                                    fontSize:
                                        "11px"
                                }}
                            >
                                SUPER_ADMIN
                            </div>

                        </div>

                        <div
                            style={{
                                width: "40px",
                                height: "40px",
                                borderRadius:
                                    "50%",
                                background:
                                    "#eff6ff",
                                color:
                                    "#2563eb",
                                display:
                                    "flex",
                                alignItems:
                                    "center",
                                justifyContent:
                                    "center",
                                fontWeight:
                                    "800"
                            }}
                        >
                            {(user?.name ||
                                "A")
                                .charAt(0)
                                .toUpperCase()}
                        </div>

                    </div>

                </div>


                {/* =================================================
                    PAGE HEADER
                   ================================================= */}

                <div
                    style={{
                        marginBottom: "25px"
                    }}
                >

                    <div
                        style={{
                            color: "#64748b",
                            fontSize: "14px",
                            marginBottom: "5px"
                        }}
                    >
                        Platform Administration
                    </div>

                    <h1
                        style={{
                            margin: 0,
                            fontSize: "30px",
                            color: "#0f172a"
                        }}
                    >
                        Super Admin Dashboard
                    </h1>

                    <p
                        style={{
                            margin:
                                "8px 0 0 0",
                            color: "#64748b"
                        }}
                    >
                        Manage organizations,
                        HR accounts,
                        subscriptions and
                        platform payments.
                    </p>

                </div>


                {/* =================================================
                    WELCOME PANEL
                   ================================================= */}

                <div
                    style={{
                        background:
                            "linear-gradient(120deg,#1e3a8a,#2563eb)",
                        borderRadius: "18px",
                        padding: "30px",
                        color: "#ffffff",
                        marginBottom: "25px",
                        position: "relative",
                        overflow: "hidden",
                        boxShadow:
                            "0 15px 30px rgba(37,99,235,0.18)"
                    }}
                >

                    <div
                        style={{
                            position:
                                "relative",
                            zIndex: 1
                        }}
                    >

                        <div
                            style={{
                                fontSize:
                                    "13px",
                                opacity:
                                    0.85,
                                marginBottom:
                                    "8px",
                                letterSpacing:
                                    "0.5px"
                            }}
                        >
                            SANDBOX PLATFORM CONTROL CENTER
                        </div>

                        <h2
                            style={{
                                margin:
                                    "0 0 10px 0",
                                fontSize:
                                    "27px"
                            }}
                        >
                            Welcome,{" "}
                            {user?.name ||
                                "Super Admin"} 👋
                        </h2>

                        <p
                            style={{
                                margin: 0,
                                maxWidth:
                                    "700px",
                                lineHeight:
                                    "1.6",
                                opacity:
                                    0.92
                            }}
                        >
                            Monitor and manage the
                            complete SandBox
                            assessment platform
                            from one centralized
                            console.
                        </p>

                    </div>

                    {/* DECORATION */}

                    <div
                        style={{
                            position:
                                "absolute",
                            width:
                                "180px",
                            height:
                                "180px",
                            borderRadius:
                                "50%",
                            border:
                                "35px solid rgba(255,255,255,0.08)",
                            right:
                                "-40px",
                            top:
                                "-60px"
                        }}
                    />

                    <div
                        style={{
                            position:
                                "absolute",
                            width:
                                "120px",
                            height:
                                "120px",
                            borderRadius:
                                "50%",
                            border:
                                "25px solid rgba(255,255,255,0.07)",
                            right:
                                "100px",
                            bottom:
                                "-70px"
                        }}
                    />

                </div>


                {/* =================================================
                    PLATFORM STATISTICS
                   ================================================= */}

                <div
                    style={{
                        display:
                            "grid",
                        gridTemplateColumns:
                            "repeat(4,minmax(0,1fr))",
                        gap:
                            "18px",
                        marginBottom:
                            "30px"
                    }}
                >

                    {/* ORGANIZATIONS */}

                    <div
                        style={{
                            background:
                                "#ffffff",
                            borderRadius:
                                "14px",
                            padding:
                                "20px",
                            border:
                                "1px solid #e2e8f0"
                        }}
                    >

                        <div
                            style={{
                                display:
                                    "flex",
                                justifyContent:
                                    "space-between",
                                alignItems:
                                    "center"
                            }}
                        >

                            <div>

                                <div
                                    style={{
                                        color:
                                            "#64748b",
                                        fontSize:
                                            "13px"
                                    }}
                                >
                                    Organizations
                                </div>

                                <div
                                    style={{
                                        fontSize:
                                            "28px",
                                        fontWeight:
                                            "800",
                                        marginTop:
                                            "5px"
                                    }}
                                >
                                    —
                                </div>

                                <div
                                    style={{
                                        color:
                                            "#94a3b8",
                                        fontSize:
                                            "11px",
                                        marginTop:
                                            "3px"
                                    }}
                                >
                                    Platform organizations
                                </div>

                            </div>

                            <div
                                style={{
                                    width:
                                        "48px",
                                    height:
                                        "48px",
                                    borderRadius:
                                        "12px",
                                    background:
                                        "#eff6ff",
                                    color:
                                        "#2563eb",
                                    display:
                                        "flex",
                                    alignItems:
                                        "center",
                                    justifyContent:
                                        "center",
                                    fontSize:
                                        "22px"
                                }}
                            >
                                ▣
                            </div>

                        </div>

                    </div>


                    {/* HR ACCOUNTS */}

                    <div
                        style={{
                            background:
                                "#ffffff",
                            borderRadius:
                                "14px",
                            padding:
                                "20px",
                            border:
                                "1px solid #e2e8f0"
                        }}
                    >

                        <div
                            style={{
                                display:
                                    "flex",
                                justifyContent:
                                    "space-between",
                                alignItems:
                                    "center"
                            }}
                        >

                            <div>

                                <div
                                    style={{
                                        color:
                                            "#64748b",
                                        fontSize:
                                            "13px"
                                    }}
                                >
                                    HR Accounts
                                </div>

                                <div
                                    style={{
                                        fontSize:
                                            "28px",
                                        fontWeight:
                                            "800",
                                        marginTop:
                                            "5px"
                                    }}
                                >
                                    —
                                </div>

                                <div
                                    style={{
                                        color:
                                            "#94a3b8",
                                        fontSize:
                                            "11px",
                                        marginTop:
                                            "3px"
                                    }}
                                >
                                    Organization administrators
                                </div>

                            </div>

                            <div
                                style={{
                                    width:
                                        "48px",
                                    height:
                                        "48px",
                                    borderRadius:
                                        "12px",
                                    background:
                                        "#f0fdf4",
                                    color:
                                        "#16a34a",
                                    display:
                                        "flex",
                                    alignItems:
                                        "center",
                                    justifyContent:
                                        "center",
                                    fontSize:
                                        "22px"
                                }}
                            >
                                ♙
                            </div>

                        </div>

                    </div>


                    {/* SUBSCRIPTIONS */}

                    <div
                        style={{
                            background:
                                "#ffffff",
                            borderRadius:
                                "14px",
                            padding:
                                "20px",
                            border:
                                "1px solid #e2e8f0"
                        }}
                    >

                        <div
                            style={{
                                display:
                                    "flex",
                                justifyContent:
                                    "space-between",
                                alignItems:
                                    "center"
                            }}
                        >

                            <div>

                                <div
                                    style={{
                                        color:
                                            "#64748b",
                                        fontSize:
                                            "13px"
                                    }}
                                >
                                    Subscriptions
                                </div>

                                <div
                                    style={{
                                        fontSize:
                                            "28px",
                                        fontWeight:
                                            "800",
                                        marginTop:
                                            "5px"
                                    }}
                                >
                                    —
                                </div>

                                <div
                                    style={{
                                        color:
                                            "#94a3b8",
                                        fontSize:
                                            "11px",
                                        marginTop:
                                            "3px"
                                    }}
                                >
                                    Active plans
                                </div>

                            </div>

                            <div
                                style={{
                                    width:
                                        "48px",
                                    height:
                                        "48px",
                                    borderRadius:
                                        "12px",
                                    background:
                                        "#fff7ed",
                                    color:
                                        "#ea580c",
                                    display:
                                        "flex",
                                    alignItems:
                                        "center",
                                    justifyContent:
                                        "center",
                                    fontSize:
                                        "22px"
                                }}
                            >
                                ◈
                            </div>

                        </div>

                    </div>


                    {/* PAYMENTS */}

                    <div
                        style={{
                            background:
                                "#ffffff",
                            borderRadius:
                                "14px",
                            padding:
                                "20px",
                            border:
                                "1px solid #e2e8f0"
                        }}
                    >

                        <div
                            style={{
                                display:
                                    "flex",
                                justifyContent:
                                    "space-between",
                                alignItems:
                                    "center"
                            }}
                        >

                            <div>

                                <div
                                    style={{
                                        color:
                                            "#64748b",
                                        fontSize:
                                            "13px"
                                    }}
                                >
                                    Payments
                                </div>

                                <div
                                    style={{
                                        fontSize:
                                            "28px",
                                        fontWeight:
                                            "800",
                                        marginTop:
                                            "5px"
                                    }}
                                >
                                    —
                                </div>

                                <div
                                    style={{
                                        color:
                                            "#94a3b8",
                                        fontSize:
                                            "11px",
                                        marginTop:
                                            "3px"
                                    }}
                                >
                                    Platform transactions
                                </div>

                            </div>

                            <div
                                style={{
                                    width:
                                        "48px",
                                    height:
                                        "48px",
                                    borderRadius:
                                        "12px",
                                    background:
                                        "#faf5ff",
                                    color:
                                        "#9333ea",
                                    display:
                                        "flex",
                                    alignItems:
                                        "center",
                                    justifyContent:
                                        "center",
                                    fontSize:
                                        "22px"
                                }}
                            >
                                ₹
                            </div>

                        </div>

                    </div>

                </div>


                {/* =================================================
                    QUICK ACTIONS
                   ================================================= */}

                <div
                    style={{
                        marginBottom:
                            "25px"
                    }}
                >

                    <h2
                        style={{
                            margin:
                                "0 0 5px 0",
                            fontSize:
                                "22px"
                        }}
                    >
                        Quick Actions
                    </h2>

                    <p
                        style={{
                            margin:
                                "0 0 18px 0",
                            color:
                                "#64748b"
                        }}
                    >
                        Access the main platform
                        administration modules.
                    </p>


                    <div
                        style={{
                            display:
                                "grid",
                            gridTemplateColumns:
                                "repeat(4,minmax(0,1fr))",
                            gap:
                                "18px"
                        }}
                    >

                        {/* ORGANIZATIONS */}

                        <button
                            onClick={() =>
                                goTo(
                                    "/admin/organizations"
                                )
                            }
                            style={{
                                background:
                                    "#ffffff",
                                border:
                                    "1px solid #e2e8f0",
                                borderRadius:
                                    "14px",
                                padding:
                                    "22px",
                                textAlign:
                                    "left",
                                cursor:
                                    "pointer",
                                boxShadow:
                                    "0 2px 6px rgba(0,0,0,0.03)"
                            }}
                        >

                            <div
                                style={{
                                    fontSize:
                                        "27px",
                                    marginBottom:
                                        "12px"
                                }}
                            >
                                🏢
                            </div>

                            <div
                                style={{
                                    fontWeight:
                                        "700",
                                    fontSize:
                                        "16px"
                                }}
                            >
                                Manage Organizations
                            </div>

                            <div
                                style={{
                                    color:
                                        "#64748b",
                                    fontSize:
                                        "12px",
                                    marginTop:
                                        "6px",
                                    lineHeight:
                                        "1.5"
                                }}
                            >
                                Create, update,
                                activate or
                                deactivate
                                organizations.
                            </div>

                        </button>


                        {/* HR */}

                        <button
                            onClick={() =>
                                goTo(
                                    "/admin/hrs"
                                )
                            }
                            style={{
                                background:
                                    "#ffffff",
                                border:
                                    "1px solid #e2e8f0",
                                borderRadius:
                                    "14px",
                                padding:
                                    "22px",
                                textAlign:
                                    "left",
                                cursor:
                                    "pointer",
                                boxShadow:
                                    "0 2px 6px rgba(0,0,0,0.03)"
                            }}
                        >

                            <div
                                style={{
                                    fontSize:
                                        "27px",
                                    marginBottom:
                                        "12px"
                                }}
                            >
                                👥
                            </div>

                            <div
                                style={{
                                    fontWeight:
                                        "700",
                                    fontSize:
                                        "16px"
                                }}
                            >
                                HR Management
                            </div>

                            <div
                                style={{
                                    color:
                                        "#64748b",
                                    fontSize:
                                        "12px",
                                    marginTop:
                                        "6px",
                                    lineHeight:
                                        "1.5"
                                }}
                            >
                                Manage HR accounts
                                associated with
                                organizations.
                            </div>

                        </button>


                        {/* SUBSCRIPTIONS */}

                        <button
                            onClick={() =>
                                goTo(
                                    "/admin/subscriptions"
                                )
                            }
                            style={{
                                background:
                                    "#ffffff",
                                border:
                                    "1px solid #e2e8f0",
                                borderRadius:
                                    "14px",
                                padding:
                                    "22px",
                                textAlign:
                                    "left",
                                cursor:
                                    "pointer",
                                boxShadow:
                                    "0 2px 6px rgba(0,0,0,0.03)"
                            }}
                        >

                            <div
                                style={{
                                    fontSize:
                                        "27px",
                                    marginBottom:
                                        "12px"
                                }}
                            >
                                💳
                            </div>

                            <div
                                style={{
                                    fontWeight:
                                        "700",
                                    fontSize:
                                        "16px"
                                }}
                            >
                                Subscription Plans
                            </div>

                            <div
                                style={{
                                    color:
                                        "#64748b",
                                    fontSize:
                                        "12px",
                                    marginTop:
                                        "6px",
                                    lineHeight:
                                        "1.5"
                                }}
                            >
                                Configure and
                                manage platform
                                subscription
                                plans.
                            </div>

                        </button>


                        {/* PAYMENTS */}

                        <button
                            onClick={() =>
                                goTo(
                                    "/admin/payments"
                                )
                            }
                            style={{
                                background:
                                    "#ffffff",
                                border:
                                    "1px solid #e2e8f0",
                                borderRadius:
                                    "14px",
                                padding:
                                    "22px",
                                textAlign:
                                    "left",
                                cursor:
                                    "pointer",
                                boxShadow:
                                    "0 2px 6px rgba(0,0,0,0.03)"
                            }}
                        >

                            <div
                                style={{
                                    fontSize:
                                        "27px",
                                    marginBottom:
                                        "12px"
                                }}
                            >
                                💰
                            </div>

                            <div
                                style={{
                                    fontWeight:
                                        "700",
                                    fontSize:
                                        "16px"
                                }}
                            >
                                Payment Monitoring
                            </div>

                            <div
                                style={{
                                    color:
                                        "#64748b",
                                    fontSize:
                                        "12px",
                                    marginTop:
                                        "6px",
                                    lineHeight:
                                        "1.5"
                                }}
                            >
                                Monitor platform
                                payment
                                transactions.
                            </div>

                        </button>

                    </div>

                </div>


                {/* =================================================
                    PLATFORM STATUS
                   ================================================= */}

                <div
                    style={{
                        background:
                            "#ffffff",
                        borderRadius:
                            "14px",
                        border:
                            "1px solid #e2e8f0",
                        padding:
                            "22px",
                        marginBottom:
                            "30px"
                    }}
                >

                    <div
                        style={{
                            display:
                                "flex",
                            justifyContent:
                                "space-between",
                            alignItems:
                                "center",
                            marginBottom:
                                "18px"
                        }}
                    >

                        <div>

                            <h3
                                style={{
                                    margin:
                                        "0 0 5px 0",
                                    fontSize:
                                        "18px"
                                }}
                            >
                                Platform Status
                            </h3>

                            <p
                                style={{
                                    margin: 0,
                                    color:
                                        "#64748b",
                                    fontSize:
                                        "13px"
                                }}
                            >
                                Current system
                                availability
                            </p>

                        </div>

                        <span
                            style={{
                                background:
                                    "#ecfdf5",
                                color:
                                    "#15803d",
                                padding:
                                    "7px 12px",
                                borderRadius:
                                    "20px",
                                fontSize:
                                    "12px",
                                fontWeight:
                                    "700"
                            }}
                        >
                            ● SYSTEM ONLINE
                        </span>

                    </div>


                    <div
                        style={{
                            display:
                                "grid",
                            gridTemplateColumns:
                                "repeat(3,minmax(0,1fr))",
                            gap:
                                "15px"
                        }}
                    >

                        <div
                            style={{
                                padding:
                                    "15px",
                                background:
                                    "#f8fafc",
                                borderRadius:
                                    "10px"
                            }}
                        >

                            <div
                                style={{
                                    color:
                                        "#64748b",
                                    fontSize:
                                        "12px"
                                }}
                            >
                                Authentication Service
                            </div>

                            <div
                                style={{
                                    color:
                                        "#16a34a",
                                    fontWeight:
                                        "700",
                                    marginTop:
                                        "5px"
                                }}
                            >
                                ● Operational
                            </div>

                        </div>


                        <div
                            style={{
                                padding:
                                    "15px",
                                background:
                                    "#f8fafc",
                                borderRadius:
                                    "10px"
                            }}
                        >

                            <div
                                style={{
                                    color:
                                        "#64748b",
                                    fontSize:
                                        "12px"
                                }}
                            >
                                Assessment Service
                            </div>

                            <div
                                style={{
                                    color:
                                        "#16a34a",
                                    fontWeight:
                                        "700",
                                    marginTop:
                                        "5px"
                                }}
                            >
                                ● Operational
                            </div>

                        </div>


                        <div
                            style={{
                                padding:
                                    "15px",
                                background:
                                    "#f8fafc",
                                borderRadius:
                                    "10px"
                            }}
                        >

                            <div
                                style={{
                                    color:
                                        "#64748b",
                                    fontSize:
                                        "12px"
                                }}
                            >
                                Proctoring Service
                            </div>

                            <div
                                style={{
                                    color:
                                        "#16a34a",
                                    fontWeight:
                                        "700",
                                    marginTop:
                                        "5px"
                                }}
                            >
                                ● Operational
                            </div>

                        </div>

                    </div>

                </div>


                {/* =================================================
                    FOOTER
                   ================================================= */}

                <div
                    style={{
                        textAlign:
                            "center",
                        color:
                            "#94a3b8",
                        fontSize:
                            "12px",
                        paddingBottom:
                            "20px"
                    }}
                >
                    © 2026 SandBox ·
                    Super Admin Console
                </div>

            </main>

        </div>
    );
}

export default Dashboard;