import {
  ArrowRight,
  CheckCircle2,
  ShieldCheck,
  Target,
  Users,
  LockKeyhole,
  ClipboardCheck,
} from "lucide-react";

import { useNavigate } from "react-router-dom";
import "./About.css";

function About() {
  const navigate = useNavigate();

  return (
    <main className="about-page">

      {/* NAVBAR */}
      <nav className="about-navbar">

        <button
          className="about-brand"
          onClick={() => navigate("/")}
        >
          <div className="about-brand-icon">
            <ShieldCheck size={22} />
          </div>

          <span>
            Sand<span>Box</span>
          </span>
        </button>

        <div className="about-nav-links">
          <button onClick={() => navigate("/")}>
            Home
          </button>

          <button className="active">
            About
          </button>
        </div>

        <div className="about-nav-actions">
          <button
            className="about-login-button"
            onClick={() => navigate("/login")}
          >
            Sign In
          </button>

          <button
            className="about-register-button"
            onClick={() => navigate("/register")}
          >
            Create Account
          </button>
        </div>

      </nav>


      {/* HERO */}
      <section className="about-hero">

        <div className="about-hero-badge">
          <ShieldCheck size={15} />
          ABOUT SANDBOX
        </div>

        <h1>
          Building a better way to
          <span> manage recruitment.</span>
        </h1>

        <p>
          SandBox is a secure recruitment management platform
          designed to help organizations structure their hiring
          process, manage candidates and conduct consistent
          evaluations through one centralized system.
        </p>

      </section>


      {/* MISSION */}
      <section className="about-mission">

        <div className="about-mission-content">

          <p className="about-eyebrow">
            OUR PURPOSE
          </p>

          <h2>
            Recruitment should be structured,
            secure and easier to manage.
          </h2>

          <p className="about-description">
            Hiring involves multiple users, candidates,
            evaluations and decisions. SandBox brings these
            workflows together while maintaining clear access
            control between administrators, HR teams and
            candidates.
          </p>

          <div className="about-checks">

            <span>
              <CheckCircle2 size={17} />
              Centralized recruitment workflows
            </span>

            <span>
              <CheckCircle2 size={17} />
              Secure role-based access
            </span>

            <span>
              <CheckCircle2 size={17} />
              Consistent candidate evaluation
            </span>

          </div>

        </div>


        <div className="about-mission-panel">

          <div className="about-panel-icon">
            <Target size={30} />
          </div>

          <h3>Our Goal</h3>

          <p>
            Provide organizations with a reliable platform
            where recruitment activities can be managed
            securely and hiring decisions can be supported
            by structured information.
          </p>

        </div>

      </section>


      {/* PRINCIPLES */}
      <section className="about-principles">

        <div className="about-section-heading">

          <p>CORE PRINCIPLES</p>

          <h2>
            Designed around the recruitment process
          </h2>

          <span>
            SandBox focuses on the areas that matter throughout
            a modern hiring workflow.
          </span>

        </div>


        <div className="about-principle-grid">

          <Principle
            icon={<LockKeyhole size={24} />}
            title="Security"
            description="Authentication, account protection and role-based access help protect recruitment data and application functionality."
          />

          <Principle
            icon={<Users size={24} />}
            title="Organization"
            description="Separate user roles and centralized workflows keep candidates and recruitment activities easier to manage."
          />

          <Principle
            icon={<ClipboardCheck size={24} />}
            title="Consistency"
            description="Structured evaluation processes help teams assess candidates through a more organized recruitment workflow."
          />

        </div>

      </section>


      {/* USERS */}
      <section className="about-users">

        <div className="about-section-heading">

          <p>ONE PLATFORM, MULTIPLE ROLES</p>

          <h2>Built for everyone in the hiring process</h2>

        </div>


        <div className="about-role-grid">

          <RoleCard
            number="01"
            title="Super Administrator"
            description="Controls platform-level administration and oversees organizations using SandBox."
          />

          <RoleCard
            number="02"
            title="HR Teams"
            description="Manage recruitment workflows, candidates and organization-level hiring activities."
          />

          <RoleCard
            number="03"
            title="Candidates"
            description="Access candidate-specific recruitment activities through a secure personal workspace."
          />

        </div>

      </section>


      {/* CTA */}
      <section className="about-cta">

        <div>
          <p>GET STARTED WITH SANDBOX</p>

          <h2>
            A structured approach to better hiring.
          </h2>

          <span>
            Create your account and get started with SandBox.
          </span>
        </div>

        <button onClick={() => navigate("/register")}>
          Create Account
          <ArrowRight size={18} />
        </button>

      </section>


      {/* FOOTER */}
      <footer className="about-footer">

        <div className="about-footer-brand">
          <ShieldCheck size={18} />
          <span>SandBox</span>
        </div>

        <p>
          Secure Recruitment Management Platform
        </p>

        <span>
          © 2026 SandBox. All rights reserved.
        </span>

      </footer>

    </main>
  );
}


function Principle({ icon, title, description }) {
  return (
    <article className="about-principle-card">

      <div className="about-principle-icon">
        {icon}
      </div>

      <h3>{title}</h3>

      <p>{description}</p>

    </article>
  );
}


function RoleCard({ number, title, description }) {
  return (
    <article className="about-role-card">

      <span className="about-role-number">
        {number}
      </span>

      <h3>{title}</h3>

      <p>{description}</p>

    </article>
  );
}

export default About;